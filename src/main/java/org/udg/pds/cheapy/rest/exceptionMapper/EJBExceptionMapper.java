package org.udg.pds.cheapy.rest.exceptionMapper;

import org.udg.pds.cheapy.util.ToJSON;

import javax.ejb.EJBException;
import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;


@Provider
public class EJBExceptionMapper implements ExceptionMapper<EJBException> {
  @Inject ToJSON toJSON;

  @Override
  public Response toResponse(EJBException e) {
      return Response.serverError().entity(toJSON.buildError("EJB error", e.getMessage())).type(MediaType.APPLICATION_JSON_TYPE).build();
  }
}

