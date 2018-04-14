package org.udg.pds.cheapy.rest;

import org.udg.pds.cheapy.model.User;
import org.udg.pds.cheapy.util.ToJSON;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

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

    protected Response accessDenied()
    {
        return Response.status(403).entity(toJSON.buildError("Error 403", "Access denied")).type(MediaType.APPLICATION_JSON_TYPE).build();
    }

    public static class ID
    {
        public Long id;

        public ID(Long id)
        {
            this.id = id;
        }
    }
}
