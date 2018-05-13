package org.udg.pds.cheapy.rest;

import org.udg.pds.cheapy.model.*;
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
    public Response deleteUser(@Context HttpServletRequest req)
    {
        userService.remove(getLoggedUser(req));

        return Response.ok().build();
    }

    @DELETE
    @Path("conversacions/{idConv}")
    public Response deleteConvers(@Context HttpServletRequest req, @PathParam("idConv") Long idC){

        Long userId = getLoggedUser(req); // obtenim id usuari en linia
        User u = userService.getUser(userId);

        Conversacio c = u.getConversa(idC); // obtenim la conversa en concret de l'usuari

        if(c == null || !c.getUsuari().getId().equals(userId)){ // si no existeix conversació o no és de l'usuari
            return accessDenied();
        }

        return buildResponseWithView(Views.Public.class, userService.esborrarConversaUsuari(idC));
    }

    @POST
    @Path("jo/conversacions/{idProd}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response creaConversacioProducte(@Context HttpServletRequest req, @PathParam("idProd") Long idProd){

        Long userId = getLoggedUser(req);
        Producte p = producteService.get(idProd); // obtenim el producte

        if(p == null){
            return accessDenied(); // no existeix el producte amb  aquesta id
        }

        return buildResponseWithView(Views.Public.class, userService.creaConversaAmbProducte(userId, p));
    }

    @DELETE
    @Path("conversacions/{idConv}/missatges/{idMiss}")
    public Response deleteMessage(@Context HttpServletRequest req, @PathParam("idConv") Long idConv, @PathParam("idMiss") Long idMiss){

        Long userId = getLoggedUser(req); // obtenim id de l'usuari en linia
        User u = userService.getUser(userId);
        Conversacio c = u.getConversa(idConv); // obtenim la conversa

        if(c == null || !c.getUsuari().getId().equals(userId)){
            return accessDenied();
        }

        Missatge m = c.getMissatge(idMiss);

        if(m == null) return accessDenied();

        return buildResponseWithView(Views.Public.class, userService.esborrarMissatgeConversa(idConv,idMiss));
    }

    @Path("/registrar")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response register(RegisterUser ru, @Context HttpServletRequest req)
    {
        checkNotLoggedIn(req);

        Ubicacio ubicacio = new Ubicacio(ru.ubicacio.coordLat, ru.ubicacio.coordLng, ru.ubicacio.ciutat, ru.ubicacio.pais);

        User usuari = userService.register(ru.nom, ru.cognoms, ru.correu, ru.contrasenya, ru.sexe, ru.telefon, ru.dataNaixement, ubicacio);

        return buildResponseWithView(Views.Private.class, usuari);
    }

    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateUser(@Valid R_User_Update nouUser, @Context HttpServletRequest req, @PathParam("id") Long id){

        Long userId = getLoggedUser(req);

        if(userService.getUser(id).equals(userService.getUser(userId))){
            try {
                userService.actualitzar(userService.getUser(id), nouUser);
                return Response.ok().build();
            }

            catch (IllegalArgumentException ex) {
                return clientError("Missing parameters in Ubicacio");
            }

        }

        return accessDenied();
    }

    @Path("/{id}/compres")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response veureProductesComprats(@Context HttpServletRequest req, @PathParam("id") Long userId, @DefaultValue("25") @QueryParam("limit") int limit, @DefaultValue("0") @QueryParam("offset") int offset)
    {
        long total = userService.totalCompres(userId);
        Data data = new Data(userService.getProductesComprats(userId, limit, offset), limit, offset, offset + limit, total);

        return buildResponseWithView(Views.Summary.class, data);
    }

    @Path("/jo/compres")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response veureProductesCompratsPropis(@Context HttpServletRequest req, @DefaultValue("25") @QueryParam("limit") int limit, @DefaultValue("0") @QueryParam("offset") int offset)
    {
        Long userID = getLoggedUser(req);
        return veureProductesComprats(req, userID, limit, offset);
    }

    @Path("/{id}/vendes")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response veureProductesVenuts(@Context HttpServletRequest req, @PathParam("id") Long userId, @DefaultValue("25") @QueryParam("limit") int limit, @DefaultValue("0") @QueryParam("offset") int offset)
    {
        long total = userService.totalVendes(userId);
        Data data = new Data(userService.getProductesVenuts(userId, limit, offset), limit, offset, offset + limit, total);

        return buildResponseWithView(Views.Summary.class, data);
    }

    @Path("/jo/vendes")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response veureProductesVenutsPropis(@Context HttpServletRequest req, @DefaultValue("25") @QueryParam("limit") int limit, @DefaultValue("0") @QueryParam("offset") int offset)
    {
        Long userID = getLoggedUser(req);
        return veureProductesVenuts(req, userID, limit, offset);
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
    public Response veureValoracions(@Context HttpServletRequest req, @PathParam("id") Long userId, @DefaultValue("25") @QueryParam("limit") int limit, @DefaultValue("0") @QueryParam("offset") int offset)
    {
        long total = userService.totalValoracions(userId);
        Data data = new Data(userService.getValoracions(userId, limit, offset), limit, offset, offset + limit, total);

        return buildResponseWithView(Views.Public.class, data);
    }

    @Path("/jo/valoracions")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response veureValoracionsPropies(@Context HttpServletRequest req, @DefaultValue("25") @QueryParam("limit") int limit, @DefaultValue("0") @QueryParam("offset") int offset)
    {
        Long userID = getLoggedUser(req);
        return veureValoracions(req, userID, limit, offset);
    }

    @Path("jo/favorits/{id}")
    @POST
    public Response afegirAFavorits(@Context HttpServletRequest req, @PathParam("id") Long productId)
    {
        Producte p = producteService.get(productId); // obtenim el producte
        Long id = getLoggedUser(req);

        userService.afegirProducteAFavorit(id, p);

        return Response.ok().build();
    }

    @Path("jo/favorits/{id}")
    @DELETE
    public Response suprimirDeFavorits(@Context HttpServletRequest req, @PathParam("id") Long productId)
    {
        Producte p = producteService.get(productId); // obtenim el producte
        Long id = getLoggedUser(req);

        userService.suprimirProducteDeFavorits(id, p);

        return Response.ok().build();
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

    public static class R_Ubicacio_Update
    {
        public String pais;
        public String ciutat;
        public Double coordLat;
        public Double coordLng;
    }

    public static class R_User_Update
    {
        public String nom;
        public String cognom;
        public String correu;
        public String contrasenya;
        public User.Sexe sexe;
        public String telefon;
        public java.util.Date dataNaixement;
        public R_Ubicacio_Update ubicacio;

    }
}

