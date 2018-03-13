package org.udg.pds.cheapy.service;

import org.udg.pds.cheapy.model.Tag;
import org.udg.pds.cheapy.rest.RESTService;

import javax.ejb.EJBException;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collection;

@Stateless
@LocalBean
public class TagService {

  @PersistenceContext
  protected EntityManager em;

  public Collection<Tag> getTags(Long id) {
    try {
      return em.createQuery("SELECT tag FROM Tag tag").getResultList();
    } catch (Exception ex) {
      // Very important: if you want that an exception reaches the EJB caller, you have to throw an EJBException
      // We catch the normal exception and then transform it in a EJBException
      throw new EJBException(ex);
    }
  }

  public Tag getTag(Long id) {
    try {
      return em.find(Tag.class, id);
    } catch (Exception ex) {
      // Very important: if you want that an exception reaches the EJB caller, you have to throw an EJBException
      // We catch the normal exception and then transform it in a EJBException
      throw new EJBException(ex);
    }
  }

  public Tag addTag(String name, String description) {
    try {
      Tag tag = new Tag(name, description);

      em.persist(tag);
      return tag;
    } catch (Exception ex) {
      // Very important: if you want that an exception reaches the EJB caller, you have to throw an EJBException
      // We catch the normal exception and then transform it in a EJBException
      throw new EJBException(ex);
    }
  }

  public RESTService.ID remove(Long tagId) {
    Tag t = em.find(Tag.class, tagId);
    em.remove(t);
    return new RESTService.ID(tagId);
  }
}
