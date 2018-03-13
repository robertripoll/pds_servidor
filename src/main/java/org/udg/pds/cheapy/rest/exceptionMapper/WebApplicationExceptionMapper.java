package org.udg.pds.cheapy.rest.exceptionMapper;

import org.udg.pds.cheapy.util.ToJSON;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;


@Provider
public class WebApplicationExceptionMapper implements ExceptionMapper<javax.ws.rs.WebApplicationException> {
  @Inject ToJSON toJSON;

  @Override
  public Response toResponse(javax.ws.rs.WebApplicationException e) {
    return Response.serverError().entity(toJSON.buildError("API error", e.getMessage())).type(MediaType.APPLICATION_JSON_TYPE).build();
  }
}

