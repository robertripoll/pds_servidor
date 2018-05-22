package org.udg.pds.cheapy.service;

import org.udg.pds.cheapy.model.Conversacio;
import org.udg.pds.cheapy.model.Missatge;
import org.udg.pds.cheapy.model.Producte;
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
        return em.createQuery("SELECT conversacio FROM conversacions conversacio WHERE conversacio.compradorConversa.id = :usuari OR conversacio.venedorConversa = :usuari")
                .setParameter("usuari", id)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }

    public Conversacio get(long id)
    {
        return em.find(Conversacio.class, id);
    }

    public Conversacio crearConversa(long userID, long prodID)
    {
        User propietariConv = em.find(User.class, userID);
        Producte p = em.find(Producte.class, prodID);
        User propietariProd = p.getVenedor();

        Conversacio c1 = new Conversacio(p, propietariConv, propietariProd); // creem la conversació
        Conversacio c2 = new Conversacio(p, propietariProd, propietariConv);

        em.persist(c1);
        em.persist(c2);

        propietariConv.addConversacioComComprador(c1);
        propietariProd.addConversacioComVenedor(c2);

        em.merge(propietariProd);
        em.merge(propietariConv);

        return c1;
    }

    public Conversacio crearConversaAutomatica(User u, Producte p){

        User propietariProducte = p.getVenedor();

        Conversacio c1 = new Conversacio(p,u,propietariProducte);

        em.persist(c1);

        u.addConversacioComComprador(c1);
        propietariProducte.addConversacioComVenedor(c1);

        em.merge(u);
        em.merge(propietariProducte);

        return c1;
    }

    public void esborrarConversa(Long id)
    {
        em.remove(get(id));
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
        return (long)em.createQuery("SELECT COUNT(conversacio) FROM conversacions conversacio WHERE conversacio.venedorConversa.id = :usuari OR conversacio.compradorConversa.id = :usuari")
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
        return em.createQuery("SELECT conversacio FROM conversacions conversacio WHERE conversacio.compradorConversa.id = :usuari AND conversacio.producte.id = :producte", Conversacio.class)
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

    public Conversacio llegirMissatges(Long id, Long userID)
    {
        em.createQuery("UPDATE missatges missatge SET missatge.estat = 'LLEGIT' WHERE missatge.conversacio.id = :conversa AND missatge.receptor.id = :receptor")
                .setParameter("conversa", id)
                .setParameter("receptor", userID)
                .executeUpdate();

        Conversacio c = conversaSimetrica(get(id));

        em.createQuery("UPDATE missatges missatge SET missatge.estat = 'LLEGIT' WHERE missatge.conversacio.id = :conversa AND missatge.emisor.id = :emisor")
                .setParameter("conversa", c.getId())
                .setParameter("emisor", c.getPropietari().getId())
                .executeUpdate();

        return get(id);
    }

    public void esborrarMissatgeConversa(Long idConv, Long idMiss)
    {
        Missatge m = em.find(Missatge.class, idMiss);
        em.remove(m);
    }
}
