package org.udg.pds.cheapy.rest;

import org.udg.pds.cheapy.model.Views;
import org.udg.pds.cheapy.service.CategoriaService;
import org.udg.pds.cheapy.util.ToJSON;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/categories")
@RequestScoped
public class CategoriaRESTService extends RESTService
{
    @EJB
    CategoriaService categoriaService;

    @Inject
    ToJSON toJSON;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll()
    {
        return buildResponseWithView(Views.Basic.class, categoriaService.getCategories());
    }
}

