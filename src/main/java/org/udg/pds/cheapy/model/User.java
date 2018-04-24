package org.udg.pds.cheapy.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.udg.pds.cheapy.rest.serializer.JsonDateDeserializer;
import org.udg.pds.cheapy.rest.serializer.JsonDateSerializer;

import javax.persistence.*;
import javax.swing.text.View;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Entity(name = "usuaris")
public class User implements Serializable
{
    public enum Sexe {
        HOME("home"), DONA("dona"), ALTRES("altres");

        private String value;

        private Sexe(String value) {
            this.value = value;
        }

        @JsonValue
        public String getValue() {
            return this.value;
        }

        @JsonCreator
        public static Sexe create(String val)
        {
            Sexe[] sexes = Sexe.values();

            for (Sexe sexe : sexes)
            {
                if (sexe.getValue().equalsIgnoreCase(val))
                    return sexe;
            }

            return ALTRES;
        }
    }

    /**
     * Default value included to remove warning. Remove or modify at will. *
     */
    private static final long serialVersionUID = 1L;

    public User()
    {
    }

    public User(Sexe sexe, String nom, String cognoms, String telefon, java.util.Date dataNaix, String correu, String contrasenya)
    {
        this.nom = nom;
        this.cognoms = cognoms;
        this.correu = correu;
        this.contrasenya = contrasenya;
        this.sexe = sexe;
        this.telefon = telefon;
        this.dataNaix = dataNaix;
    }

    public User(Sexe sexe, String nom, String cognoms, String telefon, java.util.Date dataNaix, String correu, String contrasenya, Ubicacio ubicacio)
    {
        this(sexe, nom, cognoms, telefon, dataNaix, correu, contrasenya);
        this.ubicacio = ubicacio;
    }

    //-------------------- ATRIBUTS DE LA CLASSE --------------------//

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.Private.class)
    protected Long id;

    @NotNull
    @JsonIgnore
    private String contrasenya;

    @NotNull
    @JsonView(Views.Private.class)
    @Column(unique = true)
    private String correu;

    @NotNull
    @JsonView(Views.Public.class)
    private String nom;

    @NotNull
    @JsonView(Views.Private.class)
    private String cognoms;

    @Basic
    @Enumerated(EnumType.STRING)
    @NotNull
    @JsonView(Views.Public.class)
    private Sexe sexe;

    @NotNull
    @JsonView(Views.Private.class)
    @Column(unique = true)
    private String telefon;

    @NotNull
    @JsonView(Views.Private.class)
    @Temporal(TemporalType.DATE)
    @JsonSerialize(using = JsonDateSerializer.class)
    @JsonDeserialize(as = JsonDateDeserializer.class)
    private java.util.Date dataNaix;

    //-------------------- ATRIBUTS AMB RELACIÓ AMB ALTRES ENTITATS --------------------//

    @OneToOne(cascade = CascadeType.ALL)
    @JsonView(Views.Private.class)
    private Ubicacio ubicacio;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "propietari")
    @JsonView(Views.Complete.class)
    private Collection<Conversacio> converses;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "valorat")
    private Collection<Valoracio> valoracions;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "comprador")
    @JsonView(Views.Complete.class)
    private Collection<Transaccio> compres;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "venedor")
    @JsonView(Views.Complete.class)
    private Collection<Transaccio> vendes;

    @OneToMany
    @JsonView(Views.Complete.class)
    private Collection<Producte> favorits;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "venedor")
    @JsonView(Views.Complete.class)
    private Collection<Producte> prodVenda;

    //-------------------- GETTERS I SETTERS --------------------//

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getContrasenya()
    {
        return contrasenya;
    }

    public void setContrasenya(String contrasenya)
    {
        this.contrasenya = contrasenya;
    }

    public String getCorreu()
    {
        return correu;
    }

    public void setCorreu(String correu)
    {
        this.correu = correu;
    }

    public String getNom()
    {
        return nom;
    }

    public void setNom(String nom)
    {
        this.nom = nom;
    }

    public String getCognoms()
    {
        return cognoms;
    }

    public void setCognoms(String cognoms)
    {
        this.cognoms = cognoms;
    }

    public Sexe getSexe()
    {
        return sexe;
    }

    public void setSexe(Sexe sexe)
    {
        this.sexe = sexe;
    }

    public String getTelefon()
    {
        return telefon;
    }

    public void setTelefon(String telefon)
    {
        this.telefon = telefon;
    }

    public java.util.Date getDataNaix()
    {
        return dataNaix;
    }

    public void setDataNaix(java.util.Date dataNaix)
    {
        this.dataNaix = dataNaix;
    }

    public Ubicacio getUbicacio()
    {
        return ubicacio;
    }

    public void setUbicacio(Ubicacio ubicacio)
    {
        this.ubicacio = ubicacio;
    }

    //-------------------- OPERACIONS AMB LES COLECCIONS --------------------//

    public Collection<Conversacio> getConverses()
    {
        converses.size();
        return converses;
    }

    public void setConverses(List<Conversacio> cv)
    {
        this.converses = cv;
    }

    public void addConversacio(Conversacio conversacio)
    {
        converses.add(conversacio);
    }

    @JsonIgnore
    public Collection<Valoracio> getValoracions()
    {
        valoracions.size();
        return valoracions;
    }

    public void setValoracions(List<Valoracio> val)
    {
        this.valoracions = val;
    }

    public void addValoracio(Valoracio valoracio)
    {
        valoracions.add(valoracio);
    }

    public Collection<Transaccio> getCompres()
    {
        compres.size();
        return compres;
    }

    public void setCompres(List<Transaccio> comp)
    {
        this.compres = comp;
    }

    public void addCompra(Transaccio compra)
    {
        compres.add(compra);
    }

    public Collection<Transaccio> getVendes()
    {
        vendes.size();
        return vendes;
    }

    public void setVendes(List<Transaccio> vend)
    {
        this.vendes = vend;
    }

    public void addVenda(Transaccio venda)
    {
        vendes.add(venda);
    }

    public Collection<Producte> getFavorits()
    {
        favorits.size();
        return favorits;
    }

    public void setFavorits(List<Producte> fav)
    {
        this.favorits = fav;
    }

    public void addFavorit(Producte prod)
    {
        favorits.add(prod);
    }

    public void removeFavorit(Producte prod)
    {
        favorits.remove(prod);
    }

    public Collection<Producte> getProdVenda()
    {
        prodVenda.size();
        return prodVenda;
    }

    public void setProdVenda(List<Producte> prodV)
    {
        this.prodVenda = prodV;
    }

    public void addProdVenda(Producte prod)
    {
        prodVenda.add(prod);
    }
}
