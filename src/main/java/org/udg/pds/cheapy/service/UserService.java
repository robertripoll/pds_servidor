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

    public User register(String nom, String cognoms, String correu, String contrasenya, User.Sexe sexe, String telefon, Date dataNaix, Ubicacio ubicacio, String imatge)
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

        if (imatge != null)
            nu.setImatge(new Imatge(imatge));

        em.persist(nu);
        return nu;
    }

    @SuppressWarnings("unchecked")
    public Collection<Valoracio> getValoracions(long id, int limit, int offset)
    {
        return em.createQuery("SELECT valoracio FROM valoracions valoracio WHERE valoracio.valorat.id = :usuari")
                .setParameter("usuari", id)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }

    @SuppressWarnings("unchecked")
    public Collection<Producte> getFavorits(long id, int limit, int offset)
    {
        return em.createQuery("SELECT favorit FROM usuaris usuari INNER JOIN usuari.favorits favorit WHERE usuari.id = :usuari")
                .setParameter("usuari", id)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
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

    @SuppressWarnings("unchecked")
    public Collection<Producte> getProductesComprats(long id, int limit, int offset)
    {
        return em.createQuery("SELECT producte FROM productes producte INNER JOIN producte.transaccio transaccio WHERE transaccio.comprador.id = :comprador")
                .setParameter("comprador", id)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }

    @SuppressWarnings("unchecked")
    public Collection<Producte> getProductesVenuts(long id, int limit, int offset)
    {
        return em.createQuery("SELECT producte FROM productes producte WHERE producte.venedor.id = :venedor AND producte.transaccio IS NOT NULL")
                .setParameter("venedor", id)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
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
        u.getConversesComComprador().size();
        u.getConversesComVenedor().size();
        u.getVendes().size();
        u.getFavorits().size();
        u.getValoracions().size();
        u.getProdVenda().size();
        u.getImatge();
        return u;
    }

    public long totalFavorits(long userID)
    {
        return (long)em.createQuery("SELECT COUNT(favorit) FROM usuaris usuari INNER JOIN usuari.favorits favorit WHERE usuari.id = :usuari")
                .setParameter("usuari", userID)
                .getSingleResult();
    }

    public long totalCompres(Long userId)
    {
        return (long)em.createQuery("SELECT COUNT(transaccio) FROM transaccions transaccio WHERE transaccio.comprador.id = :usuari")
                .setParameter("usuari", userId)
                .getSingleResult();
    }

    public long totalVendes(Long userId)
    {
        return (long)em.createQuery("SELECT COUNT(transaccio) FROM transaccions transaccio WHERE transaccio.venedor.id = :usuari")
                .setParameter("usuari", userId)
                .getSingleResult();
    }

    public long totalValoracions(Long userId)
    {
        return (long)em.createQuery("SELECT COUNT(valoracio) FROM valoracions valoracio WHERE valoracio.valorat.id = :usuari")
                .setParameter("usuari", userId)
                .getSingleResult();
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
            if (nouUser.imatge != null)
            {
                if (u.getImatge() != null) {
                    Imatge old = u.getImatge();
                    Imatge newImatge = new Imatge(nouUser.imatge);
                    em.persist(newImatge);
                    u.setImatge(newImatge);
                    em.remove(em.merge(old));
                }

                else {
                    Imatge i = new Imatge(nouUser.imatge);
                    em.persist(i);
                    u.setImatge(i);
                }
            }

            return em.merge(u);
        }
        catch (Exception ex){
            throw new EJBException(ex);
        }
    }

    public void setToken(Long loggedUserId, String token) {
        User u = em.find(User.class, loggedUserId);
        u.setToken(token);
    }
}
