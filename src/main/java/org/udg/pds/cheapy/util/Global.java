package org.udg.pds.cheapy.util;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.udg.pds.cheapy.model.Categoria;
import org.udg.pds.cheapy.model.Producte;
import org.udg.pds.cheapy.service.CategoriaService;
import org.udg.pds.cheapy.service.ProducteService;
import org.udg.pds.cheapy.service.UserService;

@Singleton
@Startup
public class Global
{
    @Inject
    private Logger logger;

    @EJB
    UserService userService;

    @EJB
    ProducteService producteService;

    @EJB
    CategoriaService categoriaService;

    @PostConstruct
    void init()
    {
        // Creació de Categories de mostra
        Categoria c = new Categoria("Motor i Accessoris");
        categoriaService.create(c);
        c = new Categoria("Electrònica");
        categoriaService.create(c);
        c = new Categoria("Esport i Oci");
        categoriaService.create(c);
        c = new Categoria("Mobles, Decoració i Jardí");
        categoriaService.create(c);
        c = new Categoria("Consoles i Videojocs");
        categoriaService.create(c);
        c = new Categoria("Llibres, Pel·lícules i Música");
        categoriaService.create(c);
        c = new Categoria("Moda i Accessoris");
        categoriaService.create(c);
        c = new Categoria("Infantil");
        categoriaService.create(c);
        c = new Categoria("Immobiliària");
        categoriaService.create(c);
        c = new Categoria("Electrodomèstics");
        categoriaService.create(c);
        c = new Categoria("Serveis");
        categoriaService.create(c);
        c = new Categoria("Altres");
        categoriaService.create(c);

        // Creació de Productes de mostra
        producteService.crear(categoriaService.get(1L), null, "Frens Brembo", 87.90, null, true, true);
        producteService.crear(categoriaService.get(11L), null, "Sicario", 150.0, null, true, false);
        producteService.crear(categoriaService.get(11L), null, "Deportaciones", 1650.0, null, false, false);

        // Prova amb tots els tipus de llançar missatges al logger
        logger.fatal("Error fatal");
        logger.error("Error");
        logger.warn("Alerta");
        logger.info("Missatge informatiu");
        logger.debug("Missatge debug");
    }
}