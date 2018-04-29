package org.udg.pds.cheapy.util;

import org.apache.log4j.Logger;
import org.udg.pds.cheapy.model.Categoria;
import org.udg.pds.cheapy.model.Ubicacio;
import org.udg.pds.cheapy.model.User;
import org.udg.pds.cheapy.service.CategoriaService;
import org.udg.pds.cheapy.service.ProducteService;
import org.udg.pds.cheapy.service.UbicacioService;
import org.udg.pds.cheapy.service.UserService;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import java.util.Date;

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

    @EJB
    UbicacioService ubicacioService;

    @PostConstruct
    void init()
    {
        // Creació de Ubicacions de mostra
        Ubicacio u = new Ubicacio(41.9794005,2.82142640,"Girona", "Catalunya");
        ubicacioService.create(u);
        u = new Ubicacio(4.71098859, -74.072092, "Bogotà", "Colòmbia");
        ubicacioService.create(u);
        u = new Ubicacio(38.9071923, -77.0368707, "Washington DC", "Estats Units");
        ubicacioService.create(u);

        // Creació de Usuaris de mostra

        userService.crear("Benito", "Camela", "seatleon84@zmail.com", "tetejohnny", new Date(2018,4,11), User.Sexe.create("HOME"), "34612345678", ubicacioService.get(1L));
        userService.crear("Pablo Emilio", "Escobar Gabiria", "escobar@colombia.com", "narco33", new Date(1975,5,3), User.Sexe.create("HOME"), "54612345678", ubicacioService.get(2L));
        userService.crear("Donald", "Trump", "admin@whitehouse.gov", "bigwall50", new Date(1954,8,16), User.Sexe.create("HOME"), "180000000", ubicacioService.get(3L));

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
        producteService.crear(categoriaService.get(1L), userService.getUser(1L), "Frens Brembo", 87.90, null, true, true);
        producteService.crear(categoriaService.get(11L), userService.getUser(2L), "Sicario", 150.0, null, true, false);
        producteService.crear(categoriaService.get(11L), userService.getUser(3L), "Deportaciones", 1650.0, null, false, false);

        // Prova amb tots els tipus de llançar missatges al logger
        logger.fatal("Error fatal");
        logger.error("Error");
        logger.warn("Alerta");
        logger.info("Missatge informatiu");
        logger.debug("Missatge debug");
    }
}