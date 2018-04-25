package org.udg.pds.cheapy.util;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.udg.pds.cheapy.model.Categoria;
import org.udg.pds.cheapy.service.CategoriaService;
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

        // Prova amb tots els tipus de llançar missatges al logger
        logger.fatal("Error fatal");
        logger.error("Error");
        logger.warn("Alerta");
        logger.info("Missatge informatiu");
        logger.debug("Missatge debug");
    }
}