package org.udg.pds.cheapy.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.udg.pds.cheapy.model.*;
import org.udg.pds.cheapy.rest.ProducteRESTService;
import org.udg.pds.cheapy.rest.RESTService;

import javax.ejb.EJBException;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Stateless
@LocalBean
public class ProducteService
{
    @PersistenceContext
    protected EntityManager em;

    private String longArrayToString(String[] array)
    {
        StringBuilder result = new StringBuilder();

        int i = 0;

        while (i < (array.length - 1))
        {
            result.append(array[i]).append(", ");
            i++;
        }

        result.append(array[i]);

        return result.toString();
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
                break;

            case "eq":
                predicate += "= " + value;
                break;
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

    private String distanceFilterToPredicate(Ubicacio u, String value)
    {
        String predicate = "DISTANCIA(ubicacio.coordLat, ubicacio.coordLng, "; // Quan es canvii el nom de la taula d'Ubicacions a "ubicacions", es podra dir ubicacio.xxx

        predicate += u.getCoordLat() + ", " + u.getCoordLng() + ")";

        predicate += " <= " + Double.valueOf(value);

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
                    predicates.add("producte.preuNegociable = " + Boolean.valueOf(filterQuery[0]));
                    break;

                case "intercanviAcceptat":
                    predicates.add("producte.intercanviAcceptat = " + Boolean.valueOf(filterQuery[0]));
                    break;

                case "nom":
                    predicates.add("producte.nom LIKE '" + filterQuery[0] + "'");
                    break;

                case "preu":
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
        StringBuilder query = new StringBuilder("SELECT producte FROM productes producte");

        if (!filters.isEmpty())
        {
            if (filters.containsKey("distancia")) {
                query.append(" INNER JOIN producte.venedor venedor ");
                query.append("INNER JOIN venedor.ubicacio ubicacio");
            }

            query.append(" WHERE ");

            List<String> predicates = filtersToPredicates(filters, ubicacio);

            int i = 0;

            while (i < (predicates.size() - 1)) {
                query.append(predicates.get(i)).append(" AND ");
                i++;
            }

            query.append(predicates.get(i));
        }

        if (sort != null) {
            query.append(" ORDER BY ");

            int i = 0;

            while (i < (sort.length - 1)) {
                String[] splitted = sort[i].split(",");

                if (splitted[1].toUpperCase().equals("ASC") || splitted[1].toUpperCase().equals("DESC"))
                    query.append("producte.").append(splitted[0]).append(" ").append(splitted[1]).append(",");

                else
                    break; // Com que el criteri d'ordenacio (ASC o DESC) no s'ha especificat correctament, sortim del bucle i no apliquem aquest criteri d'ordenacio

                i++;
            }

            String[] splitted = sort[i].split(",");

            if (splitted[1].toUpperCase().equals("ASC") || splitted[1].toUpperCase().equals("DESC"))
                query.append("producte.").append(splitted[0]).append(" ").append(splitted[1]);
        }

        return query.toString();
    }

    public List<Producte> getProductesEnVenda(int limit, int offset, Map<String, String[]> filters, String[] sort, Ubicacio ubicacio)
    {
        try
        {
            filters.remove("limit");
            filters.remove("offset");
            filters.remove("sort");
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

            if (nouProducte.categoria != null)
            {
                Categoria novaCategoria = em.find(Categoria.class, nouProducte.categoria.id);
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

    public RESTService.ID cancelarVenda(Long idProducte){

        Producte p = em.find(Producte.class, idProducte);

        Transaccio t = p.getTransaccio();

        if(t != null) { // si hi ha transacció llavors obtenim les valoracions i la borrem
            
            Valoracio vComprador = t.getValoracioComprador();
            Valoracio vVenedor = t.getValoracioVenedor();
            if(vComprador != null) em.remove(vComprador);
            if(vVenedor != null) em.remove(vVenedor);
            em.remove(t);
        }

        return new RESTService.ID(idProducte);
    }

    public Producte vendre(Producte p, User venedor, ProducteRESTService.R_Transaccio t)
    {
        Transaccio transaccio;

        if (t.comprador == null)
            transaccio = new Transaccio(venedor);

        else
        {
            User comprador = em.find(User.class, t.comprador.id);
            Valoracio valVenedor;

            if (t.valoracioVenedor.comentaris != null)
                valVenedor = new Valoracio(venedor, comprador, t.valoracioVenedor.estrelles, t.valoracioVenedor.comentaris);

            else
                valVenedor = new Valoracio(venedor, comprador, t.valoracioVenedor.estrelles);

            em.persist(valVenedor);
            transaccio = new Transaccio(venedor, comprador, valVenedor);
        }

        em.persist(transaccio);
        p.setTransaccio(transaccio);
        return em.merge(p);
    }

    public Transaccio valorarTransaccio(Producte p, User comprador, ProducteRESTService.R_Valoracio v)
    {
        Valoracio valoracio;

        if (v.comentaris == null)
            valoracio = new Valoracio(comprador, p.getVenedor(), v.estrelles);

        else
            valoracio = new Valoracio(comprador, p.getVenedor(), v.estrelles, v.comentaris);

        em.persist(valoracio);

        Transaccio transaccio = p.getTransaccio();
        transaccio.setValoracioComprador(valoracio);

        return em.merge(transaccio);
    }
}
