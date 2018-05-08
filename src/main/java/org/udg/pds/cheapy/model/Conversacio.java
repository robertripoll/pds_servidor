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
    @JsonView(Views.Basic.class)
    protected Long id;

    @ManyToOne
    @JsonView(Views.Basic.class)
    private User usuari;

    @ManyToOne
    @JsonIgnore
    private User propietari;

    @OneToMany(mappedBy = "conversacio")
    @JsonIgnore
    private Collection<Missatge> missatges;

    public Conversacio()
    {

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
