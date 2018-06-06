package org.udg.pds.cheapy.util;

import de.bytefish.fcmjava.client.FcmClient;
import de.bytefish.fcmjava.http.client.IFcmClient;
import de.bytefish.fcmjava.http.options.IFcmClientSettings;
import de.bytefish.fcmjava.model.options.FcmMessageOptions;
import de.bytefish.fcmjava.requests.notification.NotificationPayload;
import de.bytefish.fcmjava.requests.notification.NotificationUnicastMessage;
import de.bytefish.fcmjava.responses.FcmMessageResponse;
import io.minio.MinioClient;
import org.apache.log4j.Logger;
import org.udg.pds.cheapy.model.*;
import org.udg.pds.cheapy.service.*;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import java.time.Duration;
import java.util.Date;

@Singleton
@Startup
public class Global
{
    @Inject
    private Logger logger;

    @EJB
    private UserService userService;

    @EJB
    private ProducteService producteService;

    @EJB
    private CategoriaService categoriaService;

    @EJB
    private UbicacioService ubicacioService;

    @EJB
    private ConversacioService conversacioService;

    
    protected MinioClient minioClient;
    protected IFcmClient clientFirebase;
    private String minioBucket;
    private String BASE_URL;
    private FcmClientSettingsTest fx;

    @PostConstruct
    void init()
    {
        try{
            // Creem els parametres per el client de firebase

            IFcmClientSettings clientSettings = new FixedFcmClientSettings("AAAACmcXDWE:APA91bG-T1k-Pd5I7ahE6rdYqCVNikbFqSGFI8fbLG9L-vutz0t9ultjE8eieyrTJNEiNRW7jc2fsttxOV2S4ROSrbYNzDSRWaNvPDywTE5AlYQFdESktORB4I2tu9YGSWkNACaBfYKE");

            // Instanciem el Client amb els parametres en aquest cas la api key

            clientFirebase = new FcmClient(clientSettings);

        }catch (Exception e){ // mostrem error per el canal d'errors
            System.err.print(e);
        }

        String minioURL = null;
        String minioAccessKey = null;
        String minioSecretKey = null;

        try {
            minioURL = System.getProperty("swarm.project.minio.url");
            minioAccessKey = System.getProperty("swarm.project.minio.access-key");
            minioSecretKey = System.getProperty("swarm.project.minio.secret-key");
            minioClient = new MinioClient(minioURL, minioAccessKey, minioSecretKey);
            minioBucket = System.getProperty("swarm.project.minio.bucket");
        } catch (Exception e) {
            logger.warn("Cannot initialize minio service with url:" + minioURL + ", access-key:" + minioAccessKey + ", secret-key:" + minioSecretKey);
        }

        if (minioBucket == null) {
            logger.warn("Cannot initialize minio bucket: " + minioBucket);
            minioClient = null;
        }

        if (System.getProperty("swarm.project.base-url") != null)
            BASE_URL = System.getProperty("swarm.project.base-url");

        else {
            String port = System.getProperty("swarm.http.port") != null ? System.getProperty("swarm.http.port") : "8080";
            BASE_URL = "http://localhost:" + port;
        }

        createData();

        // Prova amb tots els tipus de llançar missatges al logger
        logger.fatal("Error fatal");
        logger.error("Error");
        logger.warn("Alerta");
        logger.info("Missatge informatiu");
        logger.debug("Missatge debug");
    }

    private void createData()
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
        categoriaService.create("Electrònica");
        categoriaService.create("Esport i Oci");
        categoriaService.create("Mobles, Decoració i Jardí");
        categoriaService.create("Consoles i Videojocs");
        categoriaService.create("Llibres, Pel·lícules i Música");
        categoriaService.create("Moda i Accessoris");
        categoriaService.create("Infantil");
        categoriaService.create("Immobiliària");
        categoriaService.create("Electrodomèstics");
        Categoria c11 = categoriaService.create("Serveis");
        categoriaService.create("Altres");

        // Creació de Productes de mostra
        Producte p1 = producteService.crear(c1, u1, "Frens Brembo", 87.90, null, true, true);
        producteService.crear(c11, u2, "Sicario", 150.0, null, true, false);
        producteService.crear(c11, u3, "Deportaciones", 1650.0, null, false, false);

        // Creació de Converses de mostra
        Conversacio conv1 = conversacioService.crearConversaAutomatica(u2,p1);

        // Creació de Missatges de mostra

        conversacioService.enviarMissatgeAutomaticament(conv1, u1.getId(), "Tinc ganes de muñeca hinchable");
        conversacioService.enviarMissatgeAutomaticament(conv1, u2.getId(), "Gas al matalas");
    }

    public MinioClient getMinioClient()
    {
        return minioClient;
    }

    public IFcmClient getFirebaseClient() { return clientFirebase;}

    public String getMinioBucket()
    {
        return minioBucket;
    }

    public String getBaseURL()
    {
        return BASE_URL;
    }

    public void enviaNotificacioMissatge(User u, Missatge m) throws Exception {

        // primer de tot definim les propietats dels missatges

        FcmMessageOptions options = FcmMessageOptions.builder()
                .setTimeToLive(Duration.ofHours(1))
                .build();

        // Creem el missatge que volem enviar

        NotificationPayload missatge = NotificationPayload.builder().setTitle("New message").setBody(m.getMissatge()).setColor("Blue").build();

        // enviem el missatge

        FcmMessageResponse response = clientFirebase.send(new NotificationUnicastMessage(options,u.getToken(), missatge));

        response.toString();
    }
}