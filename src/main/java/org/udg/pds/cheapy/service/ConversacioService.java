package org.udg.pds.cheapy.service;

import org.udg.pds.cheapy.model.Conversacio;
import org.udg.pds.cheapy.model.Missatge;

import javax.ejb.EJBException;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Stateless
@LocalBean
public class ConversacioService
{
    @PersistenceContext
    protected EntityManager em;

    @SuppressWarnings("unchecked")
    public List<Conversacio> getConversacions(long id, int limit, int offset)
    {
        return em.createQuery("SELECT conversacio FROM conversacions conversacio WHERE conversacio.propietari.id = :usuari")
                .setParameter("usuari", id)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
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

    public long totalConverses(Long userId)
    {
        return (long)em.createQuery("SELECT COUNT(conversacio) FROM conversacions conversacio WHERE conversacio.propietari.id = :usuari")
                .setParameter("usuari", userId)
                .getSingleResult();
    }

    public long totalMissatges(Long id)
    {
        return (long)em.createQuery("SELECT COUNT(missatge) FROM missatges missatge WHERE missatge.conversacio.id = :conversacio")
                .setParameter("conversacio", id)
                .getSingleResult();
    }

    public Conversacio llegirMissatges(Long id, Long userID)
    {
        em.createQuery("UPDATE missatges missatge SET missatge.estat = 'LLEGIT' WHERE missatge.conversacio.id = :conversa AND missatge.receptor.id = :receptor")
                .setParameter("conversa", id)
                .setParameter("receptor", userID)
                .executeUpdate();

        return get(id);
    }
}
