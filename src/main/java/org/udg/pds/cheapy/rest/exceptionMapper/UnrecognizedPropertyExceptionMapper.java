package org.udg.pds.cheapy.rest.exceptionMapper;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import org.udg.pds.cheapy.util.ToJSON;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class UnrecognizedPropertyExceptionMapper implements ExceptionMapper<UnrecognizedPropertyException>
{
    @Inject
    ToJSON toJSON;

    @Override
    public Response toResponse(UnrecognizedPropertyException e)
    {
        return Response.serverError().entity(toJSON.buildError("Validation error", e.toString())).type(MediaType.APPLICATION_JSON_TYPE).build();
    }
}

