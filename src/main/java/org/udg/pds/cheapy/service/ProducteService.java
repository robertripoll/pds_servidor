package org.udg.pds.cheapy.service;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import javax.persistence.criteria.*;
import java.util.*;

@Stateless
@LocalBean
public class ProducteService
{
    @PersistenceContext
    protected EntityManager em;

    private Long[] toLongArray(String[] stringArray)
    {
        Long[] result = new Long[stringArray.length];

        for (int i = 0; i < stringArray.length; i++)
            result[i] = Long.parseLong(stringArray[i]);

        return result;
    }

    private Predicate operatorToPredicate(CriteriaBuilder builder, Root<Producte> producte, String operator, Double value)
    {
        Predicate predicate = null;

        switch (operator)
        {
            case "lt":
                predicate = builder.lessThan(producte.get("preu"), value);
                break;

            case "gt":
                predicate = builder.greaterThan(producte.get("preu"), value);
                break;

            case "eq":
                predicate = builder.equal(producte.get("preu"), value);
                break;
        }

        return predicate;
    }

    private Predicate operatorsToPredicate(CriteriaBuilder builder, Root<Producte> producte, Map<String, Double> filters)
    {
        Predicate predicate = null;

        if (filters.containsKey("eq")) // Hi ha un operador que es "="
        {
            Predicate equal = operatorToPredicate(builder, producte, "eq", filters.get("eq"));
            Predicate relational = null;

            if (filters.containsKey("lt"))
                relational = operatorToPredicate(builder, producte, "lt", filters.get("lt"));

            else if (filters.containsKey("gt"))
                relational = operatorToPredicate(builder, producte, "gt", filters.get("gt"));

            predicate = builder.and(equal, relational);
        } else
        { // No hi ha cap operador "="
            Double firstValue = filters.get("lt");
            Double secondValue = filters.get("gt");

            predicate = builder.between(producte.get("preu"), firstValue, secondValue);
        }

        return predicate;
    }

    private Predicate toPricePredicate(CriteriaBuilder builder, Root<Producte> producte, String filter)
    {
        Predicate predicate = null;

        try
        {
            Map<String, Double> filters;

            ObjectMapper objectMapper = new ObjectMapper();
            filters = objectMapper.readValue(filter, HashMap.class);

            if (filters.size() == 1)
            { // Nomes es vol ">" o "<"
                String operator = filters.keySet().iterator().next();
                Double value = filters.get(operator);

                predicate = operatorToPredicate(builder, producte, operator, value);
            } else
            { // Es vol ">=" o "<=" o "> && <"
                predicate = operatorsToPredicate(builder, producte, filters);
            }
        }

        catch (Exception ex) // El filtre no es cap cadena en format JSON
        {
            predicate = operatorToPredicate(builder, producte, "eq", Double.valueOf(filter));
        }

        return predicate;
    }

    private List<Predicate> filtersToPredicates(CriteriaBuilder builder, Root<Producte> producte, Map<String, String[]> filters)
    {
        List<Predicate> predicates = new ArrayList<>();

        for (String filter : filters.keySet())
        {
            String[] filterQuery = filters.get(filter);

            switch (filter)
            {
                case "categoria":
                    predicates.add(producte.get("categoria").in(toLongArray(filterQuery))); //in(1L, 14L, 15L));
                    break;

                case "venedor":
                    predicates.add(producte.get("venedor").in(toLongArray(filterQuery))); //in(1L, 14L, 15L));
                    break;

                case "preuNegociable":
                    predicates.add(builder.equal(producte.get("preuNegociable"), Boolean.valueOf(filterQuery[0])));
                    break;

                case "intercanviAcceptat":
                    predicates.add(builder.equal(producte.get("intercanviAcceptat"), Boolean.valueOf(filterQuery[0])));
                    break;

                case "nom":
                    predicates.add(builder.like(producte.get("nom"), "%" + filterQuery[0] + "%"));
                    break;

                case "preu":
                    predicates.add(toPricePredicate(builder, producte, filterQuery[0]));
                    break;
            }
        }

        return predicates;
    }

    public List<Object> getProductesEnVenda(int limit, int offset, Map<String, String[]> filters, String[] sort)
    {
        try
        {
            CriteriaBuilder builder = em.getCriteriaBuilder();

            CriteriaQuery<Object> query = builder.createQuery();
            Root<Producte> producte = query.from(Producte.class);

            CriteriaQuery<Object> selectQuery = query.select(producte);

            List<Predicate> predicates = filtersToPredicates(builder, producte, filters);
            predicates.add(builder.isNull(producte.get("transaccio")));

            if (!predicates.isEmpty())
                query.where(builder.and(predicates.toArray(new Predicate[predicates.size()])));

            if (sort != null)
            {
                for (String criteria : sort)
                {
                    String[] splitted = criteria.split(",");

                    if (splitted[1].equals("asc"))
                        selectQuery.orderBy(builder.asc(producte.get(splitted[0])));

                    else if (splitted[1].equals("desc"))
                        selectQuery.orderBy(builder.desc(producte.get(splitted[0])));
                }
            }

            TypedQuery<Object> typedQuery = em.createQuery(selectQuery);
            typedQuery.setFirstResult(offset);
            typedQuery.setMaxResults(limit);

            return typedQuery.getResultList();
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
        }
        catch (Exception ex)
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
        }
        catch (Exception ex)
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
