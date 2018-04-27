package org.udg.pds.cheapy.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.udg.pds.cheapy.model.Categoria;
import org.udg.pds.cheapy.model.Producte;
import org.udg.pds.cheapy.model.Ubicacio;
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

    private String longArrayToString(String[] array)
    {
        String result = "";

        int i = 0;

        while (i < (array.length - 1))
        {
            result += array[i] + ", ";
            i++;
        }

        result += array[i];

        return result;
    }

    private String operatorToPredicate(String operator, Double value)
    {
        String predicate = "";

        switch (operator)
        {
            case "lt":
                predicate += "< " + value;
                break;

            case "gt":
                predicate += "> " + value;

            case "eq":
                predicate += "= " + value;
        }

        return predicate;
    }

    private List<String> operatorsToPredicates(Map<String, Double> filters)
    {
        List<String> predicates = new ArrayList<>();

        for (String filter : filters.keySet())
            predicates.add(operatorToPredicate(filter, filters.get(filter)));

        return predicates;
    }

    private List<String> priceFilterToPredicates(String filter)
    {
        List<String> predicates = new ArrayList<>();

        try
        {
            Map<String, Double> filters;

            ObjectMapper parser = new ObjectMapper();
            filters = parser.readValue(filter, HashMap.class);

            predicates.addAll(operatorsToPredicates(filters));
        }

        catch (Exception ex) // El filtre no es cap cadena en format JSON
        {
            predicates.add("producte.preu " + operatorToPredicate("eq", Double.valueOf(filter)));
        }

        return predicates;
    }

    private String distanceFilterToPredicate(Ubicacio ubicacio, String value)
    {
        String predicate = "DISTANCIA(ubicac.coordLat, ubicac.coordLng, "; // Quan es canvii el nom de la taula d'Ubicacions a "ubicacions", es podra dir ubicacio.xxx

        predicate += ubicacio.getCoordLat() + ", " + ubicacio.getCoordLng() + ")";

        predicate += " <= " + Double.valueOf(value); // += value

        return predicate;
    }

    private List<String> filtersToPredicates(Map<String, String[]> filters, Ubicacio ubicacio)
    {
        List<String> predicates = new ArrayList<>();

        for (String filter : filters.keySet())
        {
            String[] filterQuery = filters.get(filter);

            switch (filter) {
                case "categoria":
                    predicates.add("producte.categoria.id IN (" + longArrayToString(filterQuery) + ")");
                    break;

                case "venedor":
                    predicates.add("venedor.id IN (" + longArrayToString(filterQuery) + ")");
                    break;

                case "preuNegociable":
                    predicates.add("producte.preuNegociable = " + filterQuery[0]); // Boolean.valueOf(filterQuery[0])
                    break;

                case "intercanviAcceptat":
                    predicates.add("producte.intercanviAcceptat = " + filterQuery[0]); // Boolean.valueOf(filterQuery[0])
                    break;

                case "nom":
                    predicates.add("producte.nom LIKE '" + filterQuery[0] + "'");
                    break;

                case "preu":
                    //predicates.addAll(priceFilterToPredicates(filterQuery[0]));
                    for (String predicate : priceFilterToPredicates(filterQuery[0]))
                        predicates.add("producte.preu " + predicate);
                    break;

                case "distancia":
                    predicates.add(distanceFilterToPredicate(ubicacio, filterQuery[0]));
                    break;
            }
        }

        return predicates;
    }

    private String filtersToQuery(Map<String, String[]> filters, String[] sort, Ubicacio ubicacio)
    {
        String query = "SELECT producte FROM productes producte";

        if (!filters.isEmpty())
        {
            if (filters.containsKey("distancia")) {
                query += " INNER JOIN producte.venedor venedor ";
                query += "INNER JOIN venedor.ubicacio ubicac";
            }

            query += " WHERE ";

            List<String> predicates = filtersToPredicates(filters, ubicacio);

            int i = 0;

            while (i < (predicates.size() - 1)) {
                query += predicates.get(i) + " AND ";
                i++;
            }

            query += predicates.get(i);
        }

        if (sort != null) { // ALERTA: Es pot entrar qualsevol criteri (diferent a ASC i DESC), que pot fer fallar la consulta
            query += " ORDER BY ";

            int i = 0;

            while (i < (sort.length - 1)) {
                String[] splitted = sort[i].split(",");
                query += splitted[0] + " " + splitted[1] + ",";
                i++;
            }

            String[] splitted = sort[i].split(",");
            query += splitted[0] + " " + splitted[1];
        }

        return query;
    }

    public List<Producte> getProductesEnVenda(int limit, int offset, Map<String, String[]> filters, String[] sort, Ubicacio ubicacio)
    {
        try
        {
            filters.remove("limit");
            filters.remove("offset");
            String query = filtersToQuery(filters, sort, ubicacio);

            TypedQuery<Producte> typedQuery = em.createQuery(query, Producte.class);
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
                Categoria novaCategoria = em.find(Categoria.class, nouProducte.idCategoria.id);
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
