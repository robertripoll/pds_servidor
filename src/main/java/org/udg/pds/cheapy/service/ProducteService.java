package org.udg.pds.cheapy.service;

import org.udg.pds.cheapy.model.Categoria;
import org.udg.pds.cheapy.model.Producte;
import org.udg.pds.cheapy.model.User;
import org.udg.pds.cheapy.rest.ProducteRESTService;
import org.udg.pds.cheapy.rest.RESTService;

import javax.ejb.EJBException;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Map;

@Stateless
@LocalBean
public class ProducteService
{
    @PersistenceContext
    protected EntityManager em;

    public List<Object> getProductesEnVenda(int limit, int offset, Map<String, String[]> filters, String[] sort)
    {
        try
        {
            CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();

            CriteriaQuery<Object> queryObj = criteriaBuilder.createQuery();
            Root<Producte> from = queryObj.from(Producte.class);

            CriteriaQuery<Object> selectQuery = queryObj.select(from);

            if (sort != null)
            {
                for (String criteria : sort)
                {
                    String[] splitted = criteria.split(",");

                    if (splitted[1].equals("asc"))
                        selectQuery.orderBy(criteriaBuilder.asc(from.get(splitted[0])));

                    else if (splitted[1].equals("desc"))
                        selectQuery.orderBy(criteriaBuilder.asc(from.get(splitted[0])));
                }
            }

            for (String filter : filters.keySet())
            {
                if (filter.equals("categoria"))
                    queryObj.where(from.get("categoria").in((Object[])filters.get(filter)));

                if (filter.equals("venedor"))
                    queryObj.where(from.get("venedor_id").in((Object[])filters.get(filter)));

                if (filter.equals("preuNegociable"))
                    queryObj.where(from.get("venedor_id").in((Object[])filters.get(filter)));

                if (filter.equals("intercanviAcceptat"))
                    queryObj.where(from.get("venedor_id").in((Object[])filters.get(filter)));

                if (filter.equals("nom"))
                    queryObj.where(from.get("venedor_id").in((Object[])filters.get(filter)));
            }

            TypedQuery<Object> ascTypedQuery = em.createQuery(selectQuery);
            return ascTypedQuery.getResultList();

            /*Query query = em.createQuery("SELECT producte FROM productes producte WHERE producte.transaccio IS NULL");
            query.setMaxResults(limit);
            query.setFirstResult(offset);

            return query.getResultList();*/
        }

        catch (Exception ex)
        {
            // Very important: if you want that an exception reaches the EJB caller, you have to throw an EJBException
            // We catch the normal exception and then transform it in a EJBException
            throw new EJBException(ex);
        }
    }

    public Producte get(Long id)
    {
        try
        {
            return em.find(Producte.class, id);
        } catch (Exception ex)
        {
            // Very important: if you want that an exception reaches the EJB caller, you have to throw an EJBException
            // We catch the normal exception and then transform it in a EJBException
            throw new EJBException(ex);
        }
    }

    public Producte crear(Categoria categoria, User venedor, String nom, Double preu, String descripcio, Boolean preuNegociable, Boolean intercanviAcceptat)
    {
        try
        {
            Producte producte = new Producte(categoria, venedor, nom, preu, descripcio, preuNegociable, intercanviAcceptat);
            em.persist(producte);
            return producte;
        } catch (Exception ex)
        {
            // Very important: if you want that an exception reaches the EJB caller, you have to throw an EJBException
            // We catch the normal exception and then transform it in a EJBException
            throw new EJBException(ex);
        }
    }

    public Producte actualitzar(Producte p, ProducteRESTService.R_Producte_Update nouProducte)
    {
        try
        {
            if (nouProducte.nom != null)
                p.setNom(nouProducte.nom);

            if (nouProducte.intercanviAcceptat != null)
                p.setIntercanviAcceptat(nouProducte.intercanviAcceptat);

            if (nouProducte.preuNegociable != null)
                p.setPreuNegociable(nouProducte.preuNegociable);

            if (nouProducte.preu != null)
                p.setPreu(nouProducte.preu);

            if (nouProducte.descripcio != null)
                p.setDescripcio(nouProducte.descripcio);

            if (nouProducte.idCategoria != null)
            {
                Categoria novaCategoria = em.find(Categoria.class, nouProducte.idCategoria);
                p.setCategoria(novaCategoria);
            }

            return em.merge(p);
        }

        catch (Exception ex)
        {
            throw new EJBException(ex);
        }
    }

    public RESTService.ID esborrar(Long idProducte)
    {
        Producte p = em.find(Producte.class, idProducte);
        em.remove(p);
        return new RESTService.ID(idProducte);
    }
}
