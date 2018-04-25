package org.udg.pds.cheapy.util;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.udg.pds.cheapy.service.UserService;

@Singleton
@Startup
public class Global
{
    @Inject
    private Logger logger;

    @EJB
    UserService userService;

    @PostConstruct
    void init()
    {
        // Aqui va la creació de dades de mostra

        // Prova amb tots els tipus de llançar missatges al logger
        logger.fatal("Error fatal");
        logger.error("Error");
        logger.warn("Alerta");
        logger.info("Missatge informatiu");
        logger.debug("Missatge debug");
    }
}