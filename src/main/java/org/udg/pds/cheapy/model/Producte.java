package org.udg.pds.cheapy.model;

import com.fasterxml.jackson.annotation.JsonView;
import org.joda.time.DateTime;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
public class Producte implements Serializable{

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.Private.class)
    protected Long id;

    @NotNull
    @JsonView(Views.Public.class)
    private String nom;

    @NotNull
    @JsonView(Views.Public.class)
    private Double preu;

    @JsonView(Views.Public.class)
    private String descripcio;

    @NotNull
    @JsonView(Views.Private.class)
    private DateTime dataPublicacio;

    @NotNull
    @JsonView(Views.Public.class)
    private Boolean preuNegociable;

    @NotNull
    @JsonView(Views.Public.class)
    private Boolean intercanviAcceptat;

    @NotNull
    @JsonView(Views.Private.class)
    private Integer numVisites;

    @NotNull
    @JsonView(Views.Public.class)
    private Boolean reservat;

    @ManyToOne(optional = false)
    private User venedor;

    @OneToOne
    private Transaccio transaccio;

    @ManyToOne(optional = false)
    private Categoria categoria;

    public Producte()
    {

    }

    public Producte(String nom, Double preu, Boolean preuNegociable, Boolean intercanviAcceptat)
    {
        this.nom                = nom;
        this.preu               = preu;
        this.preuNegociable     = preuNegociable;
        this.intercanviAcceptat = intercanviAcceptat;

        this.numVisites         = 0;
        this.dataPublicacio     = new DateTime();
    }

    public Producte(String nom, Double preu, String descripcio, Boolean preuNegociable, Boolean intercanviAcceptat)
    {
        this(nom, preu, preuNegociable, intercanviAcceptat);
        this.descripcio = descripcio;
    }

    public Long getId()
    {
        return id;
    }

    public String getNom()
    {
        return nom;
    }

    public Double getPreu()
    {
        return preu;
    }

    public String getDescripcio()
    {
        return descripcio;
    }

    public DateTime getDataPublicacio()
    {
        return dataPublicacio;
    }

    public Boolean getPreuNegociable()
    {
        return preuNegociable;
    }

    public Boolean getIntercanviAcceptat()
    {
        return intercanviAcceptat;
    }

    public Integer getNumVisites()
    {
        return numVisites;
    }

    public Boolean getReservat()
    {
        return reservat;
    }

    public void setNom(String nouNom)
    {
        this.nom = nouNom;
    }

    public void setPreu(Double nouPreu)
    {
        this.preu = nouPreu;
    }

    public void setDescripcio(String novaDescripio)
    {
        this.descripcio = novaDescripio;
    }

    public void setPreuNegociable(Boolean nouPreuNegociable)
    {
        this.preuNegociable = nouPreuNegociable;
    }

    public void setIntercanviAcceptat(Boolean nouIntercanviAcceptat)
    {
        this.intercanviAcceptat = nouIntercanviAcceptat;
    }

    public void incrementarVisites()
    {
        numVisites++;
    }

    public void setReservat(Boolean nouReservat)
    {
        this.reservat = nouReservat;
    }

    public Categoria getCategoria()
    {
        return categoria;
    }

    public User getVenedor()
    {
        return venedor;
    }

    public Transaccio getTransaccio()
    {
        return transaccio;
    }
}
