SET foreign_key_checks = 0;

-- Funcio de càlcul de distància

DROP FUNCTION IF EXISTS distancia;
CREATE FUNCTION distancia
  (
    lat1 DOUBLE,
    lng1 DOUBLE,
    lat2 DOUBLE,
    lng2 DOUBLE
  )
  RETURNS DOUBLE
  RETURN (SELECT (6371 * ACOS(
      COS(RADIANS(lat2))
      * COS(RADIANS(lat1))
      * COS(RADIANS(lng1) - RADIANS(lng2))
      + SIN(RADIANS(lat2))
        * SIN(RADIANS(lat1))
  )) AS distance);

-- DADES DE MOSTRA

-- Ubicacions
-- INSERT INTO ubicacions (id, ciutat, pais, coordLat, coordLng) VALUES (1, 'Girona', 'Catalunya', 41.9794005, 2.82142640);
-- INSERT INTO ubicacions (id, ciutat, pais, coordLat, coordLng) VALUES (2, 'Bogotà', 'Colòmbia', 4.71098859, -74.072092);
-- INSERT INTO ubicacions (id, ciutat, pais, coordLat, coordLng) VALUES (3, 'Washington DC', 'Estats Units', 38.9071923, -77.0368707);

-- Usuaris
-- INSERT INTO usuaris (nom, cognoms, correu, contrasenya, dataNaix, sexe, telefon, ubicacio_id) VALUES ('Benito', 'Camela', 'seatleon84@zmail.com', 'tetejohnny', STR_TO_DATE('11/04/2018', '%d/%m/%Y'), 'HOME', '34612345678', 1);
-- INSERT INTO usuaris (nom, cognoms, correu, contrasenya, dataNaix, sexe, telefon, ubicacio_id) VALUES ('Pablo Emilio', 'Escobar Gabiria', 'escobar@colombia.com', 'narcos33', STR_TO_DATE('03/05/1975', '%d/%m/%Y'), 'HOME', '54612345678', 2);
-- INSERT INTO usuaris (nom, cognoms, correu, contrasenya, dataNaix, sexe, telefon, ubicacio_id) VALUES ('Donald', 'Trump', 'admin@whitehouse.gov', 'bigwall50', STR_TO_DATE('16/08/1954', '%d/%m/%Y'), 'HOME', '180000000', 3);

-- Categories
-- INSERT INTO categories (nom) VALUES ('Motor i Accessoris');
-- INSERT INTO categories (nom) VALUES ('Electrònica');
-- INSERT INTO categories (nom) VALUES ('Esport i Oci');
-- INSERT INTO categories (nom) VALUES ('Mobles, Decoració i Jardí');
-- INSERT INTO categories (nom) VALUES ('Consoles i Videojocs');
-- INSERT INTO categories (nom) VALUES ('Llibres, Pel·lícules i Música');
-- INSERT INTO categories (nom) VALUES ('Moda i Accessoris');
-- INSERT INTO categories (nom) VALUES ('Infantil');
-- INSERT INTO categories (nom) VALUES ('Immobiliària');
-- INSERT INTO categories (nom) VALUES ('Electrodomèstics');
-- INSERT INTO categories (nom) VALUES ('Serveis');
-- INSERT INTO categories (nom) VALUES ('Altres');

-- Productes
-- INSERT INTO productes (dataPublicacio, intercanviAcceptat, nom, numVisites, preu, preuNegociable, reservat, categoria_id, venedor_id) VALUES (STR_TO_DATE('11/04/2018', '%d/%m/%Y'), TRUE, 'Frens Brembo', 0, 87.90, TRUE, FALSE, 1, 1);
-- INSERT INTO productes (dataPublicacio, intercanviAcceptat, nom, numVisites, preu, preuNegociable, reservat, categoria_id, venedor_id) VALUES (STR_TO_DATE('15/04/2018', '%d/%m/%Y'), FALSE, 'Sicario', 0, 150.00, TRUE, FALSE, 11, 2);
-- INSERT INTO productes (dataPublicacio, intercanviAcceptat, nom, numVisites, preu, preuNegociable, reservat, categoria_id, venedor_id) VALUES (STR_TO_DATE('15/04/2018', '%d/%m/%Y'), FALSE, 'Deportaciones', 0, 1650.00, FALSE, FALSE, 11, 3);

-- Converses
INSERT INTO conversacio (propietari_id, usuari_id) VALUES (1, 2);
INSERT INTO conversacio (propietari_id, usuari_id) VALUES (2, 1);

-- Missatges
INSERT INTO missatge (estat, missatge, conversacio_id, emisor_id, receptor_id) VALUES ("ENVIAT", "Tinc ganes de muñeca hinchable", 1, 1, 2);
INSERT INTO missatge (estat, missatge, conversacio_id, emisor_id, receptor_id) VALUES ("ENVIAT", "Tinc ganes de muñeca hinchable", 2, 1, 2);
INSERT INTO missatge (estat, missatge, conversacio_id, emisor_id, receptor_id) VALUES ("ENVIAT", "Gas al matalas", 1, 1, 1);
INSERT INTO missatge (estat, missatge, conversacio_id, emisor_id, receptor_id) VALUES ("ENVIAT", "Gas al matalas", 2, 1, 2);

SET foreign_key_checks = 1;