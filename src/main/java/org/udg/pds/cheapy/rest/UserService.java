package org.udg.pds.cheapy.service;

import org.udg.pds.cheapy.model.User;
import org.udg.pds.cheapy.rest.RESTService;

import javax.ejb.EJBException;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
@LocalBean
public class UserService {

  @PersistenceContext
  private EntityManager em;

  public User matchPassword(String correu, String contrasenya) {
    Query q = em.createQuery("select u from User u where u.correu=:correu");
    q.setParameter("correu", correu);

    User u;
    try {
      u = (User) q.getSingleResult();
    } catch (Exception e) {
      throw new EJBException("No user with this username exists");
    }

    if (u.getContrasenya().equals(contrasenya))
      return u;
    else
      throw new EJBException("Password does not match");
  }

  public User register(String nom, String correu, String contrasenya) {

    Query q = em.createQuery("select u from User u where u.correu=:correu");
    q.setParameter("correu", correu);
    try {
      User u = (User) q.getSingleResult();
    } catch (Exception e) {
      throw new EJBException("Email already exist");
    }

    q = em.createQuery("select u from User u where u.nom=:nom");
    q.setParameter("nom", nom);
    try {
      User u = (User) q.getSingleResult();
    } catch (Exception e) {
      throw new EJBException("Username already exists");
    }

    User nu = new User(nom, correu, contrasenya);
    em.persist(nu);
    return nu;
  }

  public User getUser(long id) {
    return em.find(User.class, id);
  }

  public RESTService.ID remove(Long userId) {
    User u = getUser(userId);
    em.remove(u);
    return new RESTService.ID(userId);
  }

  public User getUserComplete(Long loggedId) {
    User u = getUser(loggedId);
    u.getCompres().size();
    u.getConverses().size();
    u.getVendes().size();
    u.getFavorits().size();
    u.getMissatges().size();
    u.getValoracions().size();
    u.getProdVenda().size();
    return u;
  }
}
