package org.udg.pds.cheapy.model;

import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;

@Entity
public class Conversacio implements Serializable
{
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.Private.class)
    protected Long id;

    @ManyToOne
    private User usuari;

    @OneToMany
    private Collection<Missatge> missatges;

    public Conversacio()
    {

    }

    public Conversacio(User usuari)
    {
        this.usuari = usuari;
    }

    public User getUsuari()
    {
        return usuari;
    }

    public Collection<Missatge> getMissatges()
    {
        return missatges;
    }
}
