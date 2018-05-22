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

    @ManyToOne(optional = false)
    @JsonView(Views.Basic.class)
    private User venedorConversa;

    @ManyToOne(optional = false)
    @JsonIgnore
    private User compradorConversa;

    @ManyToOne(optional = false)
    @JsonView(Views.Basic.class)
    private Producte producte;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "conversacio")
    private Collection<Missatge> missatges;

    @Formula("(SELECT COUNT(missatge.id) FROM missatges missatge WHERE missatge.conversacio_id = id)")
    @JsonView(Views.Basic.class)
    private Integer nombreMissatges;

    @OneToOne
    @JsonView(Views.Basic.class)
    private Missatge ultimMissatge;

    @Formula("(SELECT COUNT(missatge.id) FROM missatges missatge WHERE missatge.conversacio_id = id AND missatge.estat NOT LIKE \"%LLEGIT%\" AND missatge.receptor_id = propietari_id)")
    @JsonView(Views.Basic.class)
    private Boolean missatgesPerLlegir;

    public Conversacio()
    {

    }

    public Conversacio(Producte producte, User propietari, User usuari)
    {
        this.producte   = producte;
        this.compradorConversa = propietari;
        this.venedorConversa = usuari;
    }

    public Long getId()
    {
        return id;
    }

    public User getUsuari()
    {
        return venedorConversa;
    }

    public User getPropietari()
    {
        return compradorConversa;
    }

    public Producte getProducte()
    {
        return producte;
    }

    public Missatge getUltimMissatge()
    {
        return ultimMissatge;
    }

    @JsonIgnore
    public Collection<Missatge> getMissatges()
    {
        return missatges;
    }

    public Missatge getMissatge(Long id){

        for(Missatge m: missatges){
            if(m.getId().equals(id)) return m;
        }

        return null;
    }

    public void addMissatge(Missatge m)
    {
        ultimMissatge = m;
        missatges.add(m);
    }

    public void deleteMissatge(Missatge m) { missatges.remove(m);}

    @Override
    public boolean equals(Object o)
    {
        return ((Conversacio)o).id.equals(this.id);
    }
}
