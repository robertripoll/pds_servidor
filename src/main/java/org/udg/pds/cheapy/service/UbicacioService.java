package org.udg.pds.cheapy.service;

import org.udg.pds.cheapy.model.Categoria;
import org.udg.pds.cheapy.model.Ubicacio;

import javax.ejb.EJBException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collection;

public class UbicacioService {
    @PersistenceContext
    protected EntityManager em;

    public Collection<Ubicacio> getUbicacions()
    {
        try
        {
            return em.createQuery("SELECT ubicacio FROM ubicacions ubicacio").getResultList();
        } catch (Exception ex)
        {
            // Very important: if you want that an exception reaches the EJB caller, you have to throw an EJBException
            // We catch the normal exception and then transform it in a EJBException
            throw new EJBException(ex);
        }
    }

    public Ubicacio get(Long id)
    {
        try
        {
            return em.find(Ubicacio.class, id);
        } catch (Exception ex)
        {
            // Very important: if you want that an exception reaches the EJB caller, you have to throw an EJBException
            // We catch the normal exception and then transform it in a EJBException
            throw new EJBException(ex);
        }
    }

    public void create(Ubicacio u)
    {
        em.persist(u);
    }
}
