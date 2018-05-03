package org.udg.pds.cheapy.service;

import org.udg.pds.cheapy.model.Ubicacio;

import javax.ejb.EJBException;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collection;

@Stateless
@LocalBean
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

    public Ubicacio create(Double coordLat, Double coordLng, String ciutat, String pais)
    {
        Ubicacio u = new Ubicacio(coordLat, coordLng, ciutat, pais);

        em.persist(u);

        return u;
    }
}
