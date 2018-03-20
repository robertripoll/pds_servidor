package org.udg.pds.cheapy.model;

import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class Producte implements Serializable{

    private static final long serialVersionUID = 1L;

    public Producte(){

    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.Private.class)
    protected Long id;

    @ManyToOne
    private User usuari_fav;

    @ManyToOne
    private  User usuari_ven;
}
