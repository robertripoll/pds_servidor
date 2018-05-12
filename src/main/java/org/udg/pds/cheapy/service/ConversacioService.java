package org.udg.pds.cheapy.service;

import org.udg.pds.cheapy.model.Conversacio;
import org.udg.pds.cheapy.model.Missatge;
import org.udg.pds.cheapy.model.User;
import org.udg.pds.cheapy.rest.ConversacioRESTService;

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

    private Conversacio conversaSimetrica(Conversacio c)
    {
        return em.createQuery("SELECT conversacio FROM conversacions conversacio WHERE conversacio.propietari.id = :usuari AND conversacio.producte.id = :producte", Conversacio.class)
                .setParameter("usuari", c.getUsuari().getId())
                .setParameter("producte", c.getProducte().getId())
                .getSingleResult();
    }

    public Missatge enviarMissatge(Long id, ConversacioRESTService.R_Missatge missatge)
    {
        Conversacio convEmisor = get(id);
        User emisor = convEmisor.getPropietari();
        User receptor = convEmisor.getUsuari();

        Missatge missEmisor = new Missatge(convEmisor, emisor, receptor, missatge.text);
        em.persist(missEmisor);
        convEmisor.addMissatge(missEmisor);

        Conversacio convReceptor = conversaSimetrica(convEmisor);
        Missatge missReceptor = missEmisor.clone(convReceptor);
        em.persist(missReceptor);
        convReceptor.addMissatge(missReceptor);

        return missEmisor;
    }
}
