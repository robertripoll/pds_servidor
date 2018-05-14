package org.udg.pds.cheapy.rest;

import org.udg.pds.cheapy.model.Conversacio;
import org.udg.pds.cheapy.model.Missatge;
import org.udg.pds.cheapy.model.Views;
import org.udg.pds.cheapy.service.ConversacioService;
import org.udg.pds.cheapy.service.ProducteService;
import org.udg.pds.cheapy.util.ToJSON;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/conversacions")
@RequestScoped
public class ConversacioRESTService extends RESTService
{
    @EJB
    ConversacioService conversacioService;

    @EJB
    ProducteService producteService;

    @Inject
    ToJSON toJSON;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response veureConverses(@Context HttpServletRequest req,
                                   @DefaultValue("25") @QueryParam("limit") int limit,
                                   @DefaultValue("0") @QueryParam("offset") int offset)
    {
        Long loggedUserId = getLoggedUser(req);

        long total = conversacioService.totalConverses(loggedUserId);
        Data data = new Data(conversacioService.getConversacions(loggedUserId, limit, offset), limit, offset, offset + limit, total);

        return buildResponseWithView(Views.Basic.class, data);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response creaConversa(@Context HttpServletRequest req, @Valid ConversacioRESTService.R_Conversa conv)
    {
        Long userId = getLoggedUser(req);
        return buildResponseWithView(Views.Basic.class, conversacioService.crearConversa(userId, conv.producte.id));
    }

    @Path("{id}")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response missatgesLlegits(@Context HttpServletRequest req, @PathParam("id") Long id)
    {
        Long userID = getLoggedUser(req);

        Conversacio c = conversacioService.get(id);

        if (!c.getPropietari().getId().equals(userID))
            return accessDenied();

        return buildResponseWithView(Views.Basic.class, conversacioService.llegirMissatges(id, userID));
    }

    @DELETE
    @Path("{id}")
    public Response deleteConvers(@Context HttpServletRequest req, @PathParam("id") Long id)
    {
        Long userId = getLoggedUser(req); // obtenim id usuari en linia
        Conversacio c = conversacioService.get(id); // obtenim la conversa en concret de l'usuari

        if (!c.getPropietari().getId().equals(userId)) // si no existeix conversació o no és de l'usuari
            return accessDenied();

        conversacioService.esborrarConversa(id);

        return Response.ok().build();
    }

    @Path("{id}/missatges")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response veureMissatges(@Context HttpServletRequest req,
                                   @PathParam("id") Long id,
                                   @DefaultValue("25") @QueryParam("limit") int limit,
                                   @DefaultValue("0") @QueryParam("offset") int offset)
    {
        Long loggedUserId = getLoggedUser(req);

        Conversacio c = conversacioService.get(id);

        if (!c.getPropietari().getId().equals(loggedUserId))
            return accessDenied();

        long total = conversacioService.totalMissatges(id);
        Data data = new Data(conversacioService.getMissatges(id, limit, offset), limit, offset, offset + limit, total);

        return buildResponseWithView(Views.Basic.class, data);
    }

    @Path("{id}/missatges")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response enviarMissatge(@Context HttpServletRequest req,
                                   @PathParam("id") Long id,
                                   @Valid R_Missatge missatge)
    {
        Long userID = getLoggedUser(req);
        Conversacio c = conversacioService.get(id);

        if (!c.getPropietari().getId().equals(userID))
            return accessDenied();

        return buildResponseWithView(Views.Basic.class, conversacioService.enviarMissatge(id, missatge));
    }

    @DELETE
    @Path("{idConv}/missatges/{idMiss}")
    public Response deleteMessage(@Context HttpServletRequest req, @PathParam("idConv") Long idConv, @PathParam("idMiss") Long idMiss)
    {
        Long userId = getLoggedUser(req); // obtenim id de l'usuari en linia
        Conversacio c = conversacioService.get(idConv); // obtenim la conversa

        if (!c.getPropietari().getId().equals(userId))
            return accessDenied();

        Missatge m = c.getMissatge(idMiss);

        conversacioService.esborrarMissatgeConversa(idConv, idMiss);

        return Response.ok().build();
    }

    public static class R_Missatge
    {
        @NotNull
        public String text;
    }

    public static class R_Conversa
    {
        @NotNull
        public ID producte;
    }
}

