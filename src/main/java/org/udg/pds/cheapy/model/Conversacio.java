package org.udg.pds.cheapy.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import org.hibernate.annotations.Formula;

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

    @OneToMany(mappedBy = "conversacio", cascade = CascadeType.ALL)
    @JsonIgnore
    private Collection<Missatge> missatges;

    @Formula("(SELECT COUNT(missatge.id) FROM missatges missatge WHERE missatge.conversacio_id = id)")
    @JsonView(Views.Basic.class)
    private Integer nombreMissatges;

    @Formula("(SELECT missatge FROM missatges missatge WHERE missatge.conversacio_id = id ORDER BY missatge.dataEnviament DESC LIMIT 1)")
    @JsonView(Views.Basic.class)
    @Transient
    private Missatge ultimMissatge;

    @Formula("(SELECT CAST(COUNT(missatge.id) AS BIT) FROM missatges missatge WHERE missatge.conversacio_id = id AND missatge.estat NOT LIKE \"%LLEGIT%\")")
    @JsonView(Views.Basic.class)
    private Boolean missatgesPerLlegir;

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
