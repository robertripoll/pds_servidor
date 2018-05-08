package org.udg.pds.cheapy.service;

import org.udg.pds.cheapy.model.Conversacio;
import org.udg.pds.cheapy.model.Missatge;
import org.udg.pds.cheapy.model.User;

import javax.ejb.EJBException;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Collection;
import java.util.List;

@Stateless
@LocalBean
public class ConversacioService
{
    @PersistenceContext
    protected EntityManager em;

    public Collection<Conversacio> getConversacions(long id){

        User u = em.find(User.class, id);
        return u.getConverses();
    }

    public Conversacio get(long id)
    {
        return em.find(Conversacio.class, id);
    }

    public List<Missatge> getMissatges(Long idConversa, int limit, int offset)
    {
        try
        {
            TypedQuery<Missatge> typedQuery = em.createQuery("SELECT m FROM missatges m WHERE m.conversacio.id = :idConversa ORDER BY m.dataEnviament DESC", Missatge.class);
            typedQuery.setFirstResult(offset);
            typedQuery.setMaxResults(limit);
            typedQuery.setParameter("idConversa", idConversa);

            return typedQuery.getResultList();
        }

        catch (Exception ex)
        {
            // Very important: if you want that an exception reaches the EJB caller, you have to throw an EJBException
            // We catch the normal exception and then transform it in a EJBException
            throw new EJBException(ex);
        }
    }
}
