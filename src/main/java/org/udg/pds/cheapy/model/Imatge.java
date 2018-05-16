package org.udg.pds.cheapy.model;

import com.fasterxml.jackson.annotation.JsonView;
import org.udg.pds.cheapy.util.ImatgeListener;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity(name = "imatges")
@EntityListeners({ ImatgeListener.class })
public class Imatge implements Serializable
{
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.Basic.class)
    protected Long id;

    @NotNull
    @JsonView(Views.Basic.class)
    private String ruta;

    public Imatge()
    {

    }

    public Imatge(String nom)
    {
        this.ruta = ruta;
    }

    public String getRuta()
    {
        return ruta;
    }
}