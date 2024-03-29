package org.udg.pds.cheapy.rest;

import io.minio.MinioClient;
import org.udg.pds.cheapy.model.*;
import org.udg.pds.cheapy.service.CategoriaService;
import org.udg.pds.cheapy.service.ProducteService;
import org.udg.pds.cheapy.service.UserService;
import org.udg.pds.cheapy.util.Global;
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

    @Inject
    Global global;

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@Context HttpServletRequest req,
                        @PathParam("id") Long id)
    {
        Long userID = getLoggedUserWithoutException(req);
        Producte p = producteService.get(id);
        User comprador = null;

        if (p.getTransaccio() != null)
            if (p.getTransaccio().getComprador() != null)
                comprador = p.getTransaccio().getComprador();

        if (p.getVenedor().getId().equals(userID))
            return buildResponseWithView(Views.Private.class, p);

        else if (comprador != null && comprador.getId().equals(userID))
            return buildResponseWithView(Views.Interactor.class, p);

        else
            return buildResponseWithView(Views.Summary.class, p);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll(@Context HttpServletRequest req, @DefaultValue("25") @QueryParam("limit") int limit, @DefaultValue("0") @QueryParam("offset") int offset)
    {
        Map<String, String[]> parameters = req.getParameterMap();
        String[] sort = null;

        Long loggedID = getLoggedUserWithoutException(req);
        Ubicacio ubicacio = null;

        if (loggedID == null)
        {
            if (parameters.containsKey("distancia"))
            {
                if (parameters.containsKey("userCoordLat") && parameters.containsKey("userCoordLng")) {
                    Double userLat = Double.valueOf(parameters.get("userCoordLat")[0]);
                    Double userLng = Double.valueOf(parameters.get("userCoordLng")[0]);
                    ubicacio = new Ubicacio(userLat, userLng, null, null);
                }

                else
                    return clientError("Missing guest location (userCoordLat and userCoordLng).");
            }
        }

        else
            ubicacio = usuariService.getUser(getLoggedUserWithoutException(req)).getUbicacio();

        if (parameters.containsKey("sort"))
            sort = parameters.get("sort");

        long total = producteService.totalDeProductesEnVenda();

        Data dades = new Data(producteService.getProductesEnVenda(limit, offset, parameters, sort, ubicacio), limit, offset, offset + limit, total);

        return buildResponseWithView(Views.Summary.class, dades);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createProduct(@Valid R_Producte producte, @Context HttpServletRequest req)
    {
        Long userId = getLoggedUser(req);
        User venedor = usuariService.getUser(userId);
        Categoria categoria = categoriaService.get(producte.categoria.id);

        if (producte.descripcio == null)
            producte.descripcio = "";

        Producte p = producteService.crear(categoria, venedor, producte.nom, producte.preu, producte.descripcio, producte.preuNegociable, producte.intercanviAcceptat);

        return buildResponseWithView(Views.Summary.class, p);
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

        if (!p.getVenedor().getId().equals(userId))
            return accessDenied();

        if (nouProducte.numVisites != null && nouProducte.numVisites - p.getNumVisites() != 1)
            return clientError("Product's views counter can only be updated by 1 unit per update");

        return buildResponseWithView(Views.Interactor.class, producteService.actualitzar(p, nouProducte));
    }

    @DELETE
    @Path("{id}")
    public Response deleteProduct(@Context HttpServletRequest req,
                                  @PathParam("id") Long id)
    {
        Long userId = getLoggedUser(req);

        Producte p = producteService.get(id);

        if (!p.getVenedor().getId().equals(userId))
            return accessDenied();

        return Response.ok().build();
    }

    @POST
    @Path("{id}/imatges")
    public Response addImatge(@Context HttpServletRequest req, @PathParam("id") Long id, @Valid R_Imatge imatge)
    {
        Long userId = getLoggedUser(req);

        Producte p = producteService.get(id);

        if (!p.getVenedor().getId().equals(userId))
            return accessDenied();

        MinioClient minioClient = global.getMinioClient();
        if (minioClient == null)
            throw new WebApplicationException("Minio client not configured");

        String[] splittedUrl = imatge.imatge.split("/");
        String fileName = splittedUrl[splittedUrl.length - 1];

        try {
            minioClient.statObject(global.getMinioBucket(), fileName);
        }

        catch (Exception ex) {
            return clientError("Image does not exist");
        }

        producteService.afegirImatge(id, imatge.imatge);

        return Response.ok().build();
    }

    @DELETE
    @Path("{prodId}/imatges/{imId}")
    public Response removeImatge(@Context HttpServletRequest req, @PathParam("prodId") Long prodId, @PathParam("imId") Long imId)
    {
        Long userId = getLoggedUser(req);

        Producte p = producteService.get(prodId);
        Imatge i = producteService.getImatge(imId);

        if (!p.getVenedor().getId().equals(userId) || !p.getImatges().contains(i))
            return accessDenied();

        producteService.removeImatge(prodId, imId);

        return Response.ok().build();
    }

    @POST
    @Path("{id}/transaccio")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response sellProduct(@Context HttpServletRequest req,
                                @PathParam("id") Long id,
                                @Valid R_Transaccio transaccio)
    {
        Long userId = getLoggedUser(req);
        User venedor = usuariService.getUser(userId);

        Producte p = producteService.get(id);

        if (!p.getVenedor().getId().equals(userId))
            return accessDenied();

        return buildResponseWithView(Views.Interactor.class, producteService.vendre(p, venedor, transaccio));
    }

    @DELETE
    @Path("{id}/transaccio")
    @Produces(MediaType.APPLICATION_JSON)
    public Response cancelTransaction(@Context HttpServletRequest req, @PathParam("id") Long id)
    {
        Long userId = getLoggedUser(req);

        Producte p = producteService.get(id);

        if (!p.getVenedor().getId().equals(userId))
            return accessDenied();

        return buildResponseWithView(Views.Interactor.class, producteService.cancelarVenda(id));
    }

    @POST
    @Path("{id}/transaccio/valoracio")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response rateTransaction(@Context HttpServletRequest req,
                                @PathParam("id") Long id,
                                @Valid R_Valoracio valoracio)
    {
        Long userId = getLoggedUser(req);
        Producte p = producteService.get(id);
        User comprador = null;

        if (p.getTransaccio() != null)
            if (p.getTransaccio().getComprador() != null)
                comprador = p.getTransaccio().getComprador();

        if (comprador != null && comprador.getId().equals(userId)) // Nomes el comprador pot valorar la transaccio
            return buildResponseWithView(Views.Interactor.class, producteService.valorarTransaccio(p, comprador, valoracio));

        return accessDenied();
    }

    @DELETE
    @Path("{id}/transaccio/valoracio")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteRating(@Context HttpServletRequest req, @PathParam("id") Long id)
    {
        Long userId = getLoggedUser(req);

        Producte p = producteService.get(id);
        Valoracio v = null;

        if (p.getTransaccio() != null)
            if (p.getTransaccio().getValoracioComprador() != null)
                v = p.getTransaccio().getValoracioComprador();

        if (v == null || !v.getValorador().getId().equals(userId))
            return accessDenied();

        return buildResponseWithView(Views.Interactor.class, producteService.esborrarValoracioComprador(id));
    }

    static class R_Imatge
    {
        @NotNull
        public String imatge;
    }

    public static class R_Valoracio
    {
        @NotNull
        public Valoracio.Estrelles estrelles;
        public String comentaris;
    }

    public static class R_Transaccio
    {
        public ID comprador;
        public R_Valoracio valoracioVenedor;
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
        public ID categoria;
    }

    public static class R_Producte_Update
    {
        public String nom;
        public Double preu;
        public String descripcio;
        public Boolean preuNegociable;
        public Boolean intercanviAcceptat;
        public Integer numVisites;
        public ID categoria;
    }
}
