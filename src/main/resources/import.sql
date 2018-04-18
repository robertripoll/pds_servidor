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

-- Usuaris
INSERT INTO usuaris (nom, cognoms, correu, contrasenya, dataNaix, sexe, telefon) VALUES ('Benito', 'Camela', 'seatleon84@zmail.com', 'tetejohnny', STR_TO_DATE('11/04/2018', '%d/%m/%Y'), 'masculí', '34612345678');
INSERT INTO usuaris (nom, cognoms, correu, contrasenya, dataNaix, sexe, telefon) VALUES ('Pablo Emilio', 'Escobar Gabiria', 'escobar@colombia.com', 'narcos33', STR_TO_DATE('03/05/1975', '%d/%m/%Y'), 'masculí', '54612345678');
INSERT INTO usuaris (nom, cognoms, correu, contrasenya, dataNaix, sexe, telefon) VALUES ('Donald', 'Trump', 'admin@whitehouse.gov', 'bigwall50', STR_TO_DATE('16/08/1954', '%d/%m/%Y'), 'masculí', '180000000');

-- Categories
INSERT INTO categories (nom) VALUES ('Motor i Accessoris');
INSERT INTO categories (nom) VALUES ('Electrònica');
INSERT INTO categories (nom) VALUES ('Esport i Oci');
INSERT INTO categories (nom) VALUES ('Mobles, Decoració i Jardí');
INSERT INTO categories (nom) VALUES ('Consoles i Videojocs');
INSERT INTO categories (nom) VALUES ('Llibres, Pel·lícules i Música');
INSERT INTO categories (nom) VALUES ('Moda i Accessoris');
INSERT INTO categories (nom) VALUES ('Infantil');
INSERT INTO categories (nom) VALUES ('Immobiliària');
INSERT INTO categories (nom) VALUES ('Electrodomèstics');
INSERT INTO categories (nom) VALUES ('Serveis');
INSERT INTO categories (nom) VALUES ('Altres');

-- Productes
INSERT INTO productes (dataPublicacio, intercanviAcceptat, nom, numVisites, preu, preuNegociable, reservat, categoria_id, venedor_id) VALUES (STR_TO_DATE('11/04/2018', '%d/%m/%Y'), TRUE, 'Frens Brembo', 0, 87.90, TRUE, FALSE, 1, 1);
INSERT INTO productes (dataPublicacio, intercanviAcceptat, nom, numVisites, preu, preuNegociable, reservat, categoria_id, venedor_id) VALUES (STR_TO_DATE('15/04/2018', '%d/%m/%Y'), FALSE, 'Sicario', 0, 150.00, TRUE, FALSE, 11, 14);
INSERT INTO productes (dataPublicacio, intercanviAcceptat, nom, numVisites, preu, preuNegociable, reservat, categoria_id, venedor_id) VALUES (STR_TO_DATE('15/04/2018', '%d/%m/%Y'), FALSE, 'Deportaciones', 0, 1650.00, FALSE, FALSE, 11, 15);