package org.udg.pds.cheapy.model;

import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class Ubicacio implements Serializable
{
    private static final long serialVersionUID = 1L;

    public Ubicacio()
    {

    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.Private.class)
    protected Long id;

    @OneToOne
    private User usuari_ub;
}
