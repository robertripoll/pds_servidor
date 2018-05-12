package org.udg.pds.cheapy.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;

@Entity(name = "conversacions")
public class Conversacio implements Serializable
{
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.Private.class)
    protected Long id;

    @ManyToOne
    private User usuari;

    @ManyToOne
    private User propietari;

    @OneToMany(mappedBy = "conversacio", cascade = CascadeType.ALL)
    private Collection<Missatge> missatges;

    public Conversacio()
    {

    }

    public Long getId(){

        return id;
    }

    public Conversacio(User propietari, User usuari)
    {
        this.propietari = propietari;
        this.usuari     = usuari;
    }

    public User getUsuari()
    {
        return usuari;
    }

    public User getPropietari()
    {
        return propietari;
    }

    @JsonIgnore
    public Collection<Missatge> getMissatges()
    {
        return missatges;
    }
}
