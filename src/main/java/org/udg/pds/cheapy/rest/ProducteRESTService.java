package org.udg.pds.cheapy.rest;

import org.udg.pds.cheapy.model.Categoria;
import org.udg.pds.cheapy.model.Producte;
import org.udg.pds.cheapy.model.User;
import org.udg.pds.cheapy.model.Views;
import org.udg.pds.cheapy.service.CategoriaService;
import org.udg.pds.cheapy.service.ProducteService;
import org.udg.pds.cheapy.service.UserService;
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
import java.util.Map;

@Path("/productes")
@RequestScoped
public class ProducteRESTService extends RESTService
{
    @EJB
    ProducteService producteService;

    @EJB
    UserService usuariService;

    @EJB
    CategoriaService categoriaService;

    @Inject
    ToJSON toJSON;

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@Context HttpServletRequest req,
                        @PathParam("id") Long id)
    {
        return buildResponseWithView(Views.Public.class, producteService.get(id));
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll(@Context HttpServletRequest req, @DefaultValue("25") @QueryParam("limit") int limit, @DefaultValue("0") @QueryParam("offset") int offset)
    {
        Map<String, String[]> parameters = req.getParameterMap();
        String[] sort = null;

        if (parameters.containsKey("sort"))
            sort = parameters.get("sort");

        //return Response.ok().build();
        return buildResponseWithView(Views.Public.class, producteService.getProductesEnVenda(limit, offset, parameters, sort));
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createProduct(@Valid R_Producte producte, @Context HttpServletRequest req)
    {
        Long userId = getLoggedUser(req);
        User venedor = usuariService.getUser(userId);
        Categoria categoria = categoriaService.get(producte.idCategoria.id);

        if (producte.descripcio == null)
            producte.descripcio = "";

        Producte p = producteService.crear(categoria, venedor, producte.nom, producte.preu, producte.descripcio, producte.preuNegociable, producte.intercanviAcceptat);

        categoriaService.afegirProducte(categoria, p);
        usuariService.afegirProducte(venedor, p);

        return buildResponseWithView(Views.Public.class, p);
    }

    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateProduct(@Valid R_Producte_Update nouProducte,
                                  @Context HttpServletRequest req,
                                  @PathParam("id") Long id)
    {
        Long userId = getLoggedUser(req);

        Producte p = producteService.get(id);

        if (p.getVenedor().getId().equals(userId))
        {
            if (nouProducte.idCategoria != null)
            {
                Categoria antiga = p.getCategoria();
                categoriaService.treureProducte(antiga, p);

                Categoria nova = categoriaService.get(nouProducte.idCategoria.id);
                categoriaService.afegirProducte(nova, p);
            }

            producteService.actualitzar(p, nouProducte);

            return Response.ok().build();
        }

        return accessDenied();
    }

    @DELETE
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteProduct(@Context HttpServletRequest req,
                                  @PathParam("id") Long id)
    {
        Long userId = getLoggedUser(req);

        Producte p = producteService.get(id);

        if (p.getVenedor().getId().equals(userId))
        {
            Categoria categoria = categoriaService.get(p.getCategoria().getId());
            categoriaService.treureProducte(categoria, p);

            usuariService.treureProducte(p.getVenedor(), p);

            return buildResponse(producteService.esborrar(id));
        }

        return accessDenied();
    }

    /*static class ID
    {
        public Long id;

        public ID(Long id)
        {
            this.id = id;
        }
    }*/

    static class R_Producte
    {
        @NotNull
        public String nom;
        @NotNull
        public Double preu;
        public String descripcio;
        @NotNull
        public Boolean preuNegociable;
        @NotNull
        public Boolean intercanviAcceptat;
        @NotNull
        public ID idCategoria;
    }

    public static class R_Producte_Update
    {
        public String nom;
        public Double preu;
        public String descripcio;
        public Boolean preuNegociable;
        public Boolean intercanviAcceptat;
        public ID idCategoria;
    }
}
