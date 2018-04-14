package org.udg.pds.cheapy.service;

import org.udg.pds.cheapy.model.Producte;
import org.udg.pds.cheapy.model.User;
import org.udg.pds.cheapy.model.Valoracio;
import org.udg.pds.cheapy.rest.RESTService;

import javax.ejb.EJBException;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Collection;

@Stateless
@LocalBean
public class UserService
{
    @PersistenceContext
    private EntityManager em;

    public User matchPassword(String correu, String contrasenya)
    {
        Query q = em.createQuery("select u from usuaris u where u.correu=:correu");
        q.setParameter("correu", correu);

        User u;
        try
        {
            u = (User) q.getSingleResult();
        } catch (Exception e)
        {
            throw new EJBException("No user with this username exists");
        }

        if (u.getContrasenya().equals(contrasenya))
            return u;
        else
            throw new EJBException("Password does not match");
    }

    public User register(String nom, String cognom, String correu, String contrasenya, String sexe, String telefon, java.util.Date dataNaix)
    {

        Query q = em.createQuery("select u from usuaris u where u.correu=:correu");
        q.setParameter("correu", correu);
        if (q.getResultList().size() > 0)
        {
            throw new EJBException("Email already exist");
        }

        q = em.createQuery("select u from usuaris u where u.telefon=:telefon");
        q.setParameter("telefon", telefon);
        if (q.getResultList().size() > 0)
        {
            throw new EJBException("Telefon already exist");
        }

        User nu = new User(nom, cognom, correu, contrasenya, sexe, telefon, dataNaix);
        em.persist(nu);
        return nu;
    }

    public Collection<Valoracio> getValoracions(long id)
    {
        User u = getUser(id);

        return u.getValoracions();
    }

    public Collection<Producte> getProductesVenda(long id)
    {
        User u = getUser(id);

        return u.getProdVenda();
    }

    public User getUser(long id)
    {
        return em.find(User.class, id);
    }

    public RESTService.ID remove(Long userId)
    {
        User u = getUser(userId);
        em.remove(u);
        return new RESTService.ID(userId);
    }

    public User getUserComplete(Long loggedId)
    {
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
