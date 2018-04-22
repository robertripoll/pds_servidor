package org.udg.pds.cheapy.rest;

import org.udg.pds.cheapy.model.*;
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
            return buildResponse(producteService.esborrar(id));
        }

        return accessDenied();
    }

    @POST
    @Path("{id}/transaccio")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response sellProduct(@Context HttpServletRequest req,
                                @PathParam("id") Long id,
                                @Valid R_Transaccio transaccio)
    {
        Long userId = getLoggedUser(req);
        User venedor = usuariService.getUser(userId);

        Producte p = producteService.get(id);

        if (!p.getVenedor().getId().equals(userId))
            return accessDenied();

        Transaccio t;

        if (transaccio.comprador == null)
            t = new Transaccio(venedor);

        else
        {
            User comprador = usuariService.getUser(transaccio.comprador.id);
            t = new Transaccio(venedor, comprador);
        }

        return buildResponseWithView(Views.Private.class, producteService.vendre(p, t));
    }

    /*static class ID
    {
        public Long id;

        public ID(Long id)
        {
            this.id = id;
        }
    }*/

    /*
    "transaccio": { // Si no hi hagués transacció (no venut) no hi hauria el que hi ha a continuació
        "id": 2445,
        "data": "2017-10-05T12:14:00",
        "comprador": {
            "id": 234,
            "nom": "Donald Trump"
        },
        "valoracio": {
            "comprador": { // Valoració feta pel comprador
            "estrelles": 4,
            "comentaris": "Nice and sweet."
        },
        "venedor": { // Valoració feta pel venedor
            "estrelles": 3,
            "comentaris": "Ha fet tard..."
        }
    },
     */

    static class R_Transaccio
    {
        public ID comprador;
    }

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
