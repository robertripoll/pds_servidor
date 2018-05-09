package org.udg.pds.cheapy.service;

import org.udg.pds.cheapy.model.*;
import org.udg.pds.cheapy.rest.RESTService;
import org.udg.pds.cheapy.rest.UserRESTService;

import javax.ejb.EJBException;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Collection;
import java.util.Date;

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

    public User register(String nom, String cognoms, String correu, String contrasenya, User.Sexe sexe, String telefon, Date dataNaix, Ubicacio ubicacio)
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

        em.persist(ubicacio);

        User nu = new User(sexe, nom, cognoms, telefon, dataNaix, correu, contrasenya, ubicacio);
        em.persist(nu);
        return nu;
    }

    public Collection<Valoracio> getValoracions(long id)
    {
        User u = getUser(id);

        return u.getValoracions();
    }

    public Collection<Producte> getFavorits(long id){

        User u = getUser(id);

        return u.getFavorits();
    }

    public Collection<Transaccio> getCompres(long id){

        User u = getUser(id);

        return u.getCompres();
    }

    public Collection<Transaccio> getVendes(long id){

        User u = getUser(id);

        return u.getVendes();
    }

    public Collection<Conversacio> getConverses(long id){

        User u = getUser(id);

        return u.getConverses();
    }

    public User crear(String nom, String cognom, String correu, String contrasenya, Date dataNaix, User.Sexe sexe, String telefon, Ubicacio ubicacio){

        try{
            User u = new User(sexe,nom,cognom,telefon,dataNaix,correu,contrasenya,ubicacio);
            em.persist(u);
            return u;
        }
        catch (Exception ex){
            throw new EJBException(ex);
        }
    }

    public Collection<Producte> afegirProducteAFavorit(long id, Producte p){

        User u = getUser(id);
        u.addFavorit(p);

        em.merge(u);

        return u.getFavorits();
    }

    public Collection<Producte> suprimirProducteDeFavorits(Long id, Producte p)
    {
        User u = getUser(id);
        u.removeFavorit(p);

        return em.merge(u).getFavorits();
    }

    public User actualitzar(User u, UserRESTService.R_User_Update nouUser) throws IllegalArgumentException {
        try{
            if(nouUser.nom != null) u.setNom(nouUser.nom);
            if(nouUser.cognom != null) u.setCognoms(nouUser.cognom);
            if(nouUser.contrasenya != null) u.setContrasenya(nouUser.contrasenya);
            if(nouUser.correu != null) u.setCorreu(nouUser.correu);
            if(nouUser.telefon != null) u.setTelefon(nouUser.telefon);
            if(nouUser.dataNaixement != null) u.setDataNaix(nouUser.dataNaixement);
            if(nouUser.sexe != null) u.setSexe(nouUser.sexe);
            if(nouUser.ubicacio != null){
                if(u.getUbicacio() == null){ // creeem una nova
                    UserRESTService.R_Ubicacio_Update ubi = nouUser.ubicacio;

                    if (ubi.ciutat != null && ubi.pais != null && ubi.coordLat != null && ubi.coordLng != null) {
                        Ubicacio novaUbicacio = new Ubicacio(ubi.coordLat, ubi.coordLng, ubi.ciutat, ubi.pais);
                        em.persist(novaUbicacio);
                        u.setUbicacio(novaUbicacio);
                    }

                    else
                        throw new IllegalArgumentException("Missing parameters in Ubicacio");
                }
                else{
                    UserRESTService.R_Ubicacio_Update ubi = nouUser.ubicacio;
                    Ubicacio userUbi = u.getUbicacio();

                    if (ubi.coordLat != null)
                        userUbi.setCoordLat(ubi.coordLat);

                    if (ubi.coordLng != null)
                        userUbi.setCoordLng(ubi.coordLng);

                    if (ubi.ciutat != null)
                        userUbi.setCiutat(ubi.ciutat);

                    if (ubi.pais != null)
                        userUbi.setPais(ubi.pais);

                    em.merge(userUbi);
                }
            }

            return em.merge(u);
        }
        catch (Exception ex){
            throw new EJBException(ex);
        }
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
        u.getValoracions().size();
        u.getProdVenda().size();
        return u;
    }
}
