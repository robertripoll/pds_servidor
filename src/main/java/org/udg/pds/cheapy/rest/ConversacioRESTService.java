package org.udg.pds.cheapy.rest;

import org.udg.pds.cheapy.model.Conversacio;
import org.udg.pds.cheapy.model.Missatge;
import org.udg.pds.cheapy.model.Views;
import org.udg.pds.cheapy.service.ConversacioService;
import org.udg.pds.cheapy.util.ToJSON;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
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

    @Path("{conversacio_id}/missatges/{missatge_id}")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response llegirMissatge(@Context HttpServletRequest req,
                                   @PathParam("conversacio_id") Long convId,
                                   @PathParam("missatge_id") Long missId)
    {
        Long userId = getLoggedUser(req);

        Conversacio c = service.get(convId);
        Missatge m = service.getMissatge(missId);

        if (!c.getPropietari().getId().equals(userId) || !m.getReceptor().getId().equals(userId))
            return accessDenied();

        return buildResponseWithView(Views.Basic.class, service.llegirMissatge(missId));
    }
}

