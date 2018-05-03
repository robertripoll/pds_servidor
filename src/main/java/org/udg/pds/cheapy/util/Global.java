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
        Ubicacio ub1 = ubicacioService.create(41.9794005,2.82142640,"Girona", "Catalunya");
        Ubicacio ub2 = ubicacioService.create(4.71098859, -74.072092, "Bogotà", "Colòmbia");
        Ubicacio ub3 = ubicacioService.create(38.9071923, -77.0368707, "Washington DC", "Estats Units");

        // Creació de Usuaris de mostra
        User u1 = userService.crear("Benito", "Camela", "seatleon84@zmail.com", "tetejohnny", new Date(2018,4,11), User.Sexe.create("HOME"), "34612345678", ub1);
        User u2 = userService.crear("Pablo Emilio", "Escobar Gabiria", "escobar@colombia.com", "narco33", new Date(1975,5,3), User.Sexe.create("HOME"), "54612345678", ub2);
        User u3 = userService.crear("Donald", "Trump", "admin@whitehouse.gov", "bigwall50", new Date(1954,8,16), User.Sexe.create("HOME"), "180000000", ub3);

        // Creació de Categories de mostra
        Categoria c1 = categoriaService.create("Motor i Accessoris");
        Categoria c2 = categoriaService.create("Electrònica");
        Categoria c3 = categoriaService.create("Esport i Oci");
        Categoria c4 = categoriaService.create("Mobles, Decoració i Jardí");
        Categoria c5 =  categoriaService.create("Consoles i Videojocs");
        Categoria c6 = categoriaService.create("Llibres, Pel·lícules i Música");
        Categoria c7 = categoriaService.create("Moda i Accessoris");
        Categoria c8 = categoriaService.create("Infantil");
        Categoria c9 = categoriaService.create("Immobiliària");
        Categoria c10 = categoriaService.create("Electrodomèstics");
        Categoria c11 = categoriaService.create("Serveis");
        Categoria c12 = categoriaService.create("Altres");
        
        // Creació de Productes de mostra
        producteService.crear(c1, u1, "Frens Brembo", 87.90, null, true, true);
        producteService.crear(c11, u2, "Sicario", 150.0, null, true, false);
        producteService.crear(c11, u3, "Deportaciones", 1650.0, null, false, false);

        // Prova amb tots els tipus de llançar missatges al logger
        logger.fatal("Error fatal");
        logger.error("Error");
        logger.warn("Alerta");
        logger.info("Missatge informatiu");
        logger.debug("Missatge debug");
    }
}