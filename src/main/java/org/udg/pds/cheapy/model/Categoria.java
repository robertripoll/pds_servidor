package org.udg.pds.cheapy.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Collection;

@Entity(name = "categories")
public class Categoria implements Serializable
{
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.Public.class)
    protected Long id;

    @NotNull
    @JsonView(Views.Public.class)
    private String nom;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "categoria")
    @JsonView(Views.Complete.class)
    private Collection<Producte> productes;

    public Categoria()
    {

    }

    public Categoria(String nom)
    {
        this.nom = nom;
    }

    public Long getId()
    {
        return id;
    }

    public String getNom()
    {
        return nom;
    }

    @JsonIgnore
    public Collection<Producte> getProductes()
    {
        productes.size();
        return productes;
    }
}