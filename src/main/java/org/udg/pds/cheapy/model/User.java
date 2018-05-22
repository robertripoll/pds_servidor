package org.udg.pds.cheapy.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.hibernate.annotations.Formula;
import org.udg.pds.cheapy.rest.serializer.JsonDateDeserializer;
import org.udg.pds.cheapy.rest.serializer.JsonDateSerializer;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Collection;
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

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.Basic.class)
    protected Long id;

    @NotNull
    @JsonIgnore
    private String contrasenya;

    @NotNull
    @JsonView(Views.Private.class)
    @Column(unique = true)
    private String correu;

    @NotNull
    @JsonView(Views.Basic.class)
    private String nom;

    @NotNull
    @JsonView(Views.Private.class)
    private String cognoms;

    @Basic
    @Enumerated(EnumType.STRING)
    @NotNull
    @JsonView(Views.Basic.class)
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

    @OneToOne(cascade = CascadeType.ALL)
    @JsonView(Views.Summary.class)
    private Ubicacio ubicacio;

    @OneToOne(orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonView(Views.Basic.class)
    private Imatge imatge;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "venedorConversa")
    @JsonIgnore
    private Collection<Conversacio> conversesComVenedor;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "compradorConversa")
    @JsonIgnore
    private Collection<Conversacio> conversesComComprador;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "valorat")
    private Collection<Valoracio> valoracions;

    @Formula("(SELECT COUNT(valoracio.id) FROM valoracions valoracio WHERE valoracio.valorat_id = id)")
    @JsonView(Views.Public.class)
    private Integer nombreValoracions;

    @Formula("(SELECT AVG(valoracio.estrelles) FROM valoracions valoracio WHERE valoracio.valorat_id = id)")
    @JsonView(Views.Public.class)
    private Double mitjanaValoracions;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "comprador")
    @JsonIgnore
    private Collection<Transaccio> compres;

    @Formula("(SELECT COUNT(transaccio.id) FROM transaccions transaccio WHERE transaccio.comprador_id = id)")
    @JsonView(Views.Public.class)
    private Integer nombreCompres;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "venedor")
    @JsonIgnore
    private Collection<Transaccio> vendes;

    @Formula("(SELECT COUNT(transaccio.id) FROM transaccions transaccio WHERE transaccio.venedor_id = id)")
    @JsonView(Views.Public.class)
    private Integer nombreVendes;

    @ManyToMany
    @JsonIgnore
    private Collection<Producte> favorits;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "venedor")
    @JsonIgnore
    private Collection<Producte> prodVenda;

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

    public User(Sexe sexe, String nom, String cognoms, String telefon, java.util.Date dataNaix, String correu, String contrasenya, Imatge i)
    {
        this(sexe, nom, cognoms, telefon, dataNaix, correu, contrasenya);
        this.imatge = i;
    }

    public User(Sexe sexe, String nom, String cognoms, String telefon, java.util.Date dataNaix, String correu, String contrasenya, Ubicacio ubicacio)
    {
        this(sexe, nom, cognoms, telefon, dataNaix, correu, contrasenya);
        this.ubicacio = ubicacio;
    }

    public User(Sexe sexe, String nom, String cognoms, String telefon, java.util.Date dataNaix, String correu, String contrasenya, Ubicacio ubicacio, Imatge i)
    {
        this(sexe, nom, cognoms, telefon, dataNaix, correu, contrasenya, i);
        this.ubicacio = ubicacio;
    }

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

    public Collection<Conversacio> getConversesComComprador()
    {
        conversesComComprador.size();
        return conversesComComprador;
    }

    public Collection<Conversacio> getConversesComVenedor(){
        conversesComVenedor.size();
        return conversesComVenedor;
    }

    public Conversacio getConversaComComprador(Long id){

        for(Conversacio c: conversesComComprador){
            if(c.getId().equals(id)) return c;
        }

        return null;
    }

    public Conversacio getConversaComVenedor(Long id){
        for(Conversacio c: conversesComVenedor){
            if(c.getId().equals(id)) return c;
        }

        return null;
    }

    public void setConversesComComprador(List<Conversacio> cv)
    {
        this.conversesComComprador = cv;
    }

    public void setConversesComVenedor(List<Conversacio> cv){
        this.conversesComVenedor = cv;
    }

    public void addConversacioComComprador(Conversacio conversacio)
    {
        conversesComComprador.add(conversacio);
        System.out.println("Correcte");
    }

    public void addConversacioComVenedor(Conversacio conversacio){
        conversesComVenedor.add(conversacio);
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

    public void removeConversationComComprador(Conversacio con) { conversesComComprador.remove(con);}

    public void removeConversationComVenedor(Conversacio con){ conversesComVenedor.remove(con);}

    public void removeMessageFromConversationComComprador(Long idConv, Long idMiss){

        for(Conversacio c: conversesComComprador){
            if(c.getId().equals(idConv)) c.deleteMissatge(c.getMissatge(idMiss));
        }
    }

    public void removeMessageFromConversationComVenedor(Long idConv, Long idMiss){
        for(Conversacio c: conversesComVenedor){
            if(c.getId().equals(idConv)) c.deleteMissatge(c.getMissatge(idMiss));
        }
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

    public Imatge getImatge()
    {
        return imatge;
    }

    public void setImatge(Imatge i)
    {
        this.imatge = i;
    }
}
