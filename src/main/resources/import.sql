-- You can use this file to load seed data into the database using SQL statements
insert into User (username, email, password) values ('jo', 'jp@hotmail.com', 'jo');
insert into User (username, email, password) values ('tu', 'tu@hotmail.com', 'tu');

-- Dades de mostra de Cheapy a inserir a la base de dades (1 usuari, 1 categoria, 1 producte)
INSERT INTO usuaris (nom, cognoms, correu, contrasenya, dataNaix, sexe, telefon) VALUES ('Benito', 'Camela', 'seatleon84@zmail.com', 'tetejohnny', STR_TO_DATE('11/04/2018', '%d/%m/%Y'), 'masculí', '34612345678');
INSERT INTO categoria (nom) VALUES ('Motor i Accessoris');
INSERT INTO productes (dataPublicacio, intercanviAcceptat, nom, numVisites, preu, preuNegociable, reservat, categoria_id, venedor_id) VALUES (STR_TO_DATE('11/04/2018', '%d/%m/%Y'), 1, 'Frens Brembo', 0, 87.90, 1, 0, 1, 1);