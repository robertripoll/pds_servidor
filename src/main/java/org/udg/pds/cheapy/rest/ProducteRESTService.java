package org.udg.pds.cheapy.rest;

import org.udg.pds.cheapy.model.Categoria;
import org.udg.pds.cheapy.model.Producte;
import org.udg.pds.cheapy.model.User;
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

@Path("/productes")
@RequestScoped
public class ProducteRESTService extends RESTService {

  @EJB
  ProducteService producteService;

  @EJB
  UserService usuariService;

  @Inject
  ToJSON toJSON;

  @GET
  @Path("{id}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response get(@Context HttpServletRequest req,
                        @PathParam("id") Long id) {
    return buildResponse(producteService.get(id));
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response getAll(@Context HttpServletRequest req) {
    return buildResponse(producteService.getProductesEnVenda());
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response createProduct(@Valid R_Producte producte, @Context HttpServletRequest req)
  {
      Long userId = getLoggedUser(req);
      User venedor = usuariService.getUser(userId);
      Categoria categoria = null; // Falta crear el servei per les Categories

      if (producte.descripcio == null) {
          producte.descripcio = "";
      }

      return buildResponse(producteService.crear(categoria, venedor, producte.nom, producte.preu, producte.preuNegociable, producte.intercanviAcceptat));
  }

  @PUT
  @Path("{id}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response updateProduct(@Valid R_Producte_Update producte,
                                @Context HttpServletRequest req,
                                @PathParam("id") Long id)
  {
      if (producte.descripcio == null)
          producte.descripcio = "";

      Categoria c = null; // Falta crear el servei Categoria
      Long userId = getLoggedUser(req);
      User venedor = usuariService.getUser(userId);

      Producte p = new Producte(c, venedor, producte.nom, producte.preu, producte.descripcio, producte.preuNegociable, producte.intercanviAcceptat);
      p.setId(id);

      producteService.actualitzar(p);

      return Response.ok().build();
  }

  @DELETE
  @Path("{id}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response deleteProduct(@Context HttpServletRequest req,
                            @PathParam("id") Long id) {

    Long userId = getLoggedUser(req);

    return buildResponse(producteService.esborrar(id));
  }

  static class ID {
    public Long id;

    public ID(Long id) {
      this.id = id;
    }
  }

  static class R_Producte {
      @NotNull public String nom;
      @NotNull public double preu;
      public String descripcio;
      @NotNull public boolean preuNegociable;
      @NotNull public boolean intercanviAcceptat;
      @NotNull public ID idCategoria;
      @NotNull public ID idVenedor;
  }

    static class R_Producte_Update {
        public String nom;
        public double preu;
        public String descripcio;
        public boolean preuNegociable;
        public boolean intercanviAcceptat;
        public ID idCategoria;
        public ID idVenedor;
    }
}
