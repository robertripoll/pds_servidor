package org.udg.pds.cheapy.rest;

import org.udg.pds.cheapy.model.User;
import org.udg.pds.cheapy.model.Views;
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
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


// This class is used to process all the authentication related URLs
@Path("/usuaris")
@RequestScoped
public class UserRESTService extends RESTService {

  // This is the EJB used to access user data
  @EJB
  UserService userService;

  @Inject
  ToJSON toJSON;

  @Path("/autenticar")
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response auth(@Context HttpServletRequest req,
                       @CookieParam("JSESSIONID") Cookie cookie,
                       @Valid LoginUser user) {

    checkNotLoggedIn(req);

    User u = userService.matchPassword(user.correu, user.contrasenya);
    req.getSession().setAttribute("simpleapp_auth_id", u.getId());
    return buildResponseWithView(Views.Private.class, u);
  }

  @Path("{id}")
  @DELETE
  @Produces(MediaType.APPLICATION_JSON)
  public Response deleteUser(@Context HttpServletRequest req, @PathParam("id") Long userId) {

    Long loggedUserId = getLoggedUser(req);

    if (!loggedUserId.equals(userId))
      throw new WebApplicationException("Cannot delet other users!");

    return buildResponse(userService.remove(userId));
  }

  @Path("/registrar")
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  public Response register(RegisterUser ru, @Context HttpServletRequest req) {

    try {
      Long userId = getLoggedUser(req);
    } catch (WebApplicationException ex) {
      return buildResponse(userService.register(ru.nom,ru.cognom, ru.correu, ru.contrasenya));
    }

    throw new WebApplicationException("Cannot register while user is logged in");
  }



  static class LoginUser {
    @NotNull public String correu;
    @NotNull public String contrasenya;
  }

  static class RegisterUser {
    @NotNull public String nom;
    @NotNull public String correu;
    @NotNull public String contrasenya;
    @NotNull public String cognom;
  }

  static class ID {
    public Long id;

    public ID(Long id) {
      this.id = id;
    }
  }

}

