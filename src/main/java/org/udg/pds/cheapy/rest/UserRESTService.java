package org.udg.pds.cheapy.rest;

import org.udg.pds.cheapy.model.Producte;
import org.udg.pds.cheapy.model.Ubicacio;
import org.udg.pds.cheapy.model.User;
import org.udg.pds.cheapy.model.Views;
import org.udg.pds.cheapy.service.ProducteService;
import org.udg.pds.cheapy.service.UserService;
import org.udg.pds.cheapy.util.ToJSON;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.awt.*;

@Path("/usuaris")
@RequestScoped
public class UserRESTService extends RESTService
{
    // This is the EJB used to access user data
    @EJB
    UserService userService;

    @EJB
    ProducteService producteService;

    @Inject
    ToJSON toJSON;

    @Path("/autenticar")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response auth(@Context HttpServletRequest req,
                         @CookieParam("JSESSIONID") Cookie cookie,
                         @Valid LoginUser user)
    {
        checkNotLoggedIn(req);

        User u = userService.matchPassword(user.correu, user.contrasenya);
        req.getSession().setAttribute("simpleapp_auth_id", u.getId());
        return buildResponseWithView(Views.Private.class, u);
    }

    @Path("/desautenticar")
    @POST
    public Response desAuth(@Context HttpServletRequest req, @Context HttpServletResponse response)
    {
        HttpSession session = req.getSession(false);
        session.removeAttribute("simpleapp_auth_id");
        session.getMaxInactiveInterval();

        return Response.ok().build();
    }

    @Path("jo/favorits")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response veureProductesFavorits(@Context HttpServletRequest req, @DefaultValue("25") @QueryParam("limit") int limit, @DefaultValue("0") @QueryParam("offset") int offset)
    {
        Long loggedUserId = getLoggedUser(req);

        long total = userService.totalFavorits(loggedUserId);
        Data data = new Data(userService.getFavorits(loggedUserId, limit, offset), limit, offset, offset + limit, total);

        return buildResponseWithView(Views.Summary.class, data);
    }

    @Path("jo")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteUser(@Context HttpServletRequest req)
    {
        return buildResponse(userService.remove(getLoggedUser(req)));
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response register(RegisterUser ru, @Context HttpServletRequest req)
    {
        checkNotLoggedIn(req);

        Ubicacio ubicacio = new Ubicacio(ru.ubicacio.coordLat, ru.ubicacio.coordLng, ru.ubicacio.ciutat, ru.ubicacio.pais);

        User usuari = userService.register(ru.nom, ru.cognoms, ru.correu, ru.contrasenya, ru.sexe, ru.telefon, ru.dataNaixement, ubicacio);

        return buildResponseWithView(Views.Private.class, usuari);
    }

    @Path("/{id}/compres")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response veureProductesComprats(@Context HttpServletRequest req, @PathParam("id") Long userId)
    {
        return buildResponseWithView(Views.Summary.class, userService.getProductesComprats(userId));
    }

    @Path("/jo/compres")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response veureProductesCompratsPropis(@Context HttpServletRequest req)
    {
        Long userID = getLoggedUser(req);
        return veureProductesComprats(req, userID);
    }

    @Path("/{id}/vendes")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response veureProductesVenuts(@Context HttpServletRequest req, @PathParam("id") Long userId)
    {
        return buildResponseWithView(Views.Summary.class, userService.getProductesVenuts(userId));
    }

    @Path("/jo/vendes")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response veureProductesVenutsPropis(@Context HttpServletRequest req)
    {
        Long userID = getLoggedUser(req);
        return veureProductesVenuts(req, userID);
    }

    @Path("{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response veurePerfil(@Context HttpServletRequest req, @PathParam("id") Long userId)
    {
        Long loggedUserId = getLoggedUserWithoutException(req);
        User u = userService.getUserComplete(userId);

        if (loggedUserId.equals(userId))
            return buildResponseWithView(Views.Private.class, u);

        return buildResponseWithView(Views.Public.class, u);
    }

    @Path("/jo")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response veurePerfilPropi(@Context HttpServletRequest req)
    {
        Long userID = getLoggedUser(req);
        return veurePerfil(req, userID);
    }

    @Path("{id}/valoracions")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response veureValoracions(@Context HttpServletRequest req, @PathParam("id") Long userId)
    {
        return buildResponseWithView(Views.Public.class, userService.getValoracions(userId));
    }

    @Path("/jo/valoracions")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response veureValoracionsPropies(@Context HttpServletRequest req)
    {
        Long userID = getLoggedUser(req);
        return veureValoracions(req, userID);
    }

    @Path("jo/favorits/{id}")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response afegirAFavorits(@Context HttpServletRequest req, @PathParam("id") Long productId)
    {
        Producte p = producteService.get(productId); // obtenim el producte
        Long id = getLoggedUser(req);

        return buildResponseWithView(Views.Summary.class, userService.afegirProducteAFavorit(id, p));
    }

    @Path("jo/favorits/{id}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response suprimirDeFavorits(@Context HttpServletRequest req, @PathParam("id") Long productId)
    {
        Producte p = producteService.get(productId); // obtenim el producte
        Long id = getLoggedUser(req);

        return buildResponseWithView(Views.Summary.class, userService.suprimirProducteDeFavorits(id,p));
    }

    static class LoginUser
    {
        @NotNull
        public String correu;
        @NotNull
        public String contrasenya;
    }

    static class R_Ubicacio
    {
        @NotNull
        public String pais;
        @NotNull
        public String ciutat;
        @NotNull
        public Double coordLat;
        @NotNull
        public double coordLng;
    }

    static class RegisterUser
    {
        @NotNull
        public String nom;
        @NotNull
        public String correu;
        @NotNull
        public String contrasenya;
        @NotNull
        public String cognoms;
        @NotNull
        public User.Sexe sexe;
        @NotNull
        public String telefon;
        @NotNull
        public java.util.Date dataNaixement;
        public R_Ubicacio ubicacio;
    }
}

