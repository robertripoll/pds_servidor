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

    @Path("/favorits")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response veureProductesFavorits(@Context HttpServletRequest req)
    {
        Long loggedUserId = getLoggedUser(req);
        return buildResponseWithView(Views.Summary.class, userService.getFavorits(loggedUserId));
    }

    @Path("{id}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteUser(@Context HttpServletRequest req, @PathParam("id") Long userId)
    {
        Long loggedUserId = getLoggedUser(req);

        if (!loggedUserId.equals(userId))
            throw new WebApplicationException("Cannot delete other users!");

        return buildResponse(userService.remove(userId));
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

    @Path("/{id}/vendes")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response veureProductesVenuts(@Context HttpServletRequest req, @PathParam("id") Long userId)
    {
        return buildResponseWithView(Views.Summary.class, userService.getProductesVenuts(userId));
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

    @Path("{id}/valoracions")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response veureValoracions(@Context HttpServletRequest req, @PathParam("id") Long userId)
    {
        return buildResponseWithView(Views.Private.class, (User) userService.getValoracions(userId));
    }

    @Path("/favorits/{id}")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response afegirAFavorits(@Context HttpServletRequest req, @PathParam("id") Long productId)
    {
        Producte p = producteService.get(productId); // obtenim el producte
        Long id = getLoggedUser(req);

        return buildResponseWithView(Views.Summary.class, userService.afegirProducteAFavorit(id, p));
    }

    @Path("/favorits/{id}")
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

