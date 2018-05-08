package org.udg.pds.cheapy.rest;

import org.udg.pds.cheapy.model.*;
import org.udg.pds.cheapy.service.ConversacioService;
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
    public Response veureConverses(@Context HttpServletRequest req)
    {
        Long loggedUserId = getLoggedUser(req);

        return buildResponseWithView(Views.Basic.class, service.getConversacions(loggedUserId));
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

        return buildResponseWithView(Views.Basic.class, service.getMissatges(id, limit, offset));
    }
}

