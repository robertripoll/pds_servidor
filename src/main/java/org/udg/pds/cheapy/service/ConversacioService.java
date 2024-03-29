package org.udg.pds.cheapy.service;

import org.udg.pds.cheapy.model.Conversacio;
import org.udg.pds.cheapy.model.Missatge;
import org.udg.pds.cheapy.model.Producte;
import org.udg.pds.cheapy.model.User;
import org.udg.pds.cheapy.rest.ConversacioRESTService;
import org.udg.pds.cheapy.util.Global;

import javax.ejb.EJBException;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
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

    @Inject
    Global global;

    @SuppressWarnings("unchecked")
    public List<Conversacio> getConversacions(long id, int limit, int offset)
    {
        return em.createQuery("SELECT conversacio FROM conversacions conversacio WHERE conversacio.compradorConversa.id = :usuari OR conversacio.venedorConversa.id = :usuari")
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
        User comprador = em.find(User.class, userID);
        Producte p = em.find(Producte.class, prodID);
        User venedor = p.getVenedor();

        Conversacio c1 = new Conversacio(p, comprador, venedor); // creem la conversació
        em.persist(c1);

        return c1;
    }

    public Conversacio crearConversaAutomatica(User u, Producte p){

        User propietariProducte = p.getVenedor();

        Conversacio c1 = new Conversacio(p,u,propietariProducte);

        em.persist(c1);

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
            TypedQuery<Missatge> typedQuery = em.createQuery("SELECT m FROM missatges m WHERE m.conversacio.id = :idConversa ORDER BY m.dataEnviament DESC, m.id DESC", Missatge.class);
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

    public Missatge enviarMissatge(Long convId, Long emisorID, ConversacioRESTService.R_Missatge missatge) throws Exception
    {
        Conversacio conv = get(convId);
        User emisor = em.find(User.class, emisorID);
        User receptor = (conv.getVenedorConversa().getId().equals(emisorID)) ? conv.getCompradorConversa() : conv.getVenedorConversa();

        Missatge missEmisor = new Missatge(conv, emisor, receptor, missatge.text);
        em.persist(missEmisor);
        conv.addMissatge(missEmisor);

        // enviem la notificació client firebase
        //FcmClient clientFirebase = global.getFirebaseClient();
        //clientFirebase.enviaNotificacioMissatge(receptor,missEmisor);

        global.enviaNotificacioMissatge(receptor,missEmisor);
        return missEmisor;
    }

    public Missatge enviarMissatgeAutomaticament(Conversacio c, Long emisorID, String missatge)
    {
        User emisor = em.find(User.class, emisorID);
        User receptor = (c.getVenedorConversa().getId().equals(emisorID)) ? c.getCompradorConversa() : c.getVenedorConversa();
        Missatge missatgeConv = new Missatge(c, emisor, receptor, missatge);
        em.persist(missatgeConv);
        c.addMissatge(missatgeConv);

        return missatgeConv;
    }

    public Conversacio llegirMissatges(Long id, Long userID)
    {
        em.createQuery("UPDATE missatges missatge SET missatge.estat = 'LLEGIT' WHERE missatge.conversacio.id = :conversa AND missatge.receptor.id = :receptor")
                .setParameter("conversa", id)
                .setParameter("receptor", userID)
                .executeUpdate();

        return get(id);
    }

    public void esborrarMissatgeConversa(Long idMiss)
    {
        Missatge m = em.find(Missatge.class, idMiss);

        Conversacio c = get(m.getConversacio().getId());

        if (c.getUltimMissatge().getId().equals(m.getId())) {
            // select * from missatges where conversacio_id = 1 and id != 2 order by id DESC limit 1;

            TypedQuery<Missatge> typedQuery = em.createQuery("SELECT m FROM missatges m WHERE m.conversacio.id = :conversacio AND m.id <> :missatge ORDER BY m.id DESC", Missatge.class);
            typedQuery.setMaxResults(1);
            typedQuery.setParameter("missatge", m.getId());
            typedQuery.setParameter("conversacio", c.getId());

            Missatge ultim = typedQuery.getSingleResult();
            c.addMissatge(ultim);

            em.persist(c);
        }

        em.remove(m);
        em.persist(c);
    }

    public Missatge getMissatge(Long idMiss)
    {
        return em.find(Missatge.class, idMiss);
    }
}
