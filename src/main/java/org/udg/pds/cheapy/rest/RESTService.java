package org.udg.pds.cheapy.rest;

import org.udg.pds.cheapy.util.ToJSON;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Collection;

/**
 * Created by imartin on 21/02/17.
 */
public class RESTService
{
    @Inject
    protected ToJSON toJSON;

    protected Long getLoggedUser(@Context HttpServletRequest req)
    {
        // Access to the HTTP session
        HttpSession session = req.getSession();

        if (session == null)
        {
            throw new WebApplicationException("No sessions available!");
        }

        Long userId = (Long) session.getAttribute("simpleapp_auth_id");
        // Check if the session has the attribute "simpleapp_auth_id"
        if (userId == null)
            throw new WebApplicationException("User is not authenticated!");

        return userId;
    }

    protected Long getLoggedUserWithoutException(@Context HttpServletRequest req)
    {
        try {
            return getLoggedUser(req);
        }

        catch (Exception ex) {
            return null;
        }
    }

    protected void checkNotLoggedIn(@Context HttpServletRequest req)
    {
        // Access to the HTTP session
        HttpSession session = req.getSession();

        if (session == null)
        {
            throw new WebApplicationException("No sessions available!");
        }

        Long userId = (Long) session.getAttribute("simpleapp_auth_id");
        // Check if the session has the attribute "simpleapp_auth_id"
        if (userId != null)
            throw new WebApplicationException("User is already authenticated!");
    }


    protected Response buildResponse(Object o)
    {
        try
        {
            return Response.ok(o).build();
        } catch (Exception e)
        {
            throw new WebApplicationException("Error serializing response with view");
        }
    }

    protected Response buildResponseWithView(Class<?> view, Object o)
    {
        try
        {
            return Response.ok(toJSON.Object(view, o)).build();
        } catch (IOException e)
        {
            throw new WebApplicationException("Error serializing response with view");
        }
    }

    protected Response clientError(String errorMessage)
    {
        return Response.status(400).entity(toJSON.buildError("Error 400", errorMessage)).type(MediaType.APPLICATION_JSON_TYPE).build();
    }

    protected Response accessDenied()
    {
        return Response.status(403).entity(toJSON.buildError("Error 403", "Access denied")).type(MediaType.APPLICATION_JSON_TYPE).build();
    }

    public static class ID
    {
        public Long id;

        public ID(){
            id = null;
        }

        public ID(Long id)
        {
            this.id = id;
        }
    }

    public static class Metadata
    {
        public Integer limit;
        public Integer currentOffset;
        public Integer nextOffset;
        public long total;

        public Metadata(int limit, int currentOffset, int nextOffset, long total)
        {
            this.limit          = limit;
            this.currentOffset  = currentOffset;
            this.nextOffset     = nextOffset;
            this.total          = total;
        }
    }

    public static class Data
    {
        public Collection<?> items;
        public Metadata metadata;

        public Data(Collection<?> items, int limit, int currentOffset, int nextOffset, long total)
        {
            this.items      = items;
            this.metadata   = new Metadata(limit, currentOffset, nextOffset, total);
        }
    }
}
