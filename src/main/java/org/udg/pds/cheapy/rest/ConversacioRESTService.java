package org.udg.pds.cheapy.rest;

import org.udg.pds.cheapy.model.Conversacio;
import org.udg.pds.cheapy.model.Views;
import org.udg.pds.cheapy.service.ConversacioService;
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
    ConversacioService service;

    @Inject
    ToJSON toJSON;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response veureConverses(@Context HttpServletRequest req,
                                   @DefaultValue("25") @QueryParam("limit") int limit,
                                   @DefaultValue("0") @QueryParam("offset") int offset)
    {
        Long loggedUserId = getLoggedUser(req);

        long total = service.totalConverses(loggedUserId);
        Data data = new Data(service.getConversacions(loggedUserId, limit, offset), limit, offset, offset + limit, total);

        return buildResponseWithView(Views.Basic.class, data);
    }

    @Path("{id}")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response missatgesLlegits(@Context HttpServletRequest req, @PathParam("id") Long id)
    {
        Long userID = getLoggedUser(req);

        Conversacio c = service.get(id);

        if (!c.getPropietari().getId().equals(userID))
            return accessDenied();

        return buildResponseWithView(Views.Basic.class, service.llegirMissatges(id, userID));
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

        Conversacio c = service.get(id);

        if (!c.getPropietari().getId().equals(loggedUserId))
            return accessDenied();

        long total = service.totalMissatges(id);
        Data data = new Data(service.getMissatges(id, limit, offset), limit, offset, offset + limit, total);

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
        Conversacio c = service.get(id);

        if (!c.getPropietari().getId().equals(userID))
            return accessDenied();

        return buildResponseWithView(Views.Basic.class, service.enviarMissatge(id, missatge));
    }

    public static class R_Missatge
    {
        @NotNull
        public String text;
    }

    public static class R_Conversa{

        @NotNull
        public ID idProd;
    }
}

