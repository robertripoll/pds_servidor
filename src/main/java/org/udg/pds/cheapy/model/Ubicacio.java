package org.udg.pds.cheapy.model;

import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
public class Ubicacio implements Serializable
{
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.Private.class)
    protected Long id;

    @NotNull
    @JsonView(Views.Private.class)
    private Double coordLat;

    @NotNull
    @JsonView(Views.Private.class)
    private Double coordLng;

    @NotNull
    @JsonView(Views.Public.class)
    private String ciutat;

    @NotNull
    @JsonView(Views.Public.class)
    private String pais;

    public Ubicacio()
    {

    }

    public Ubicacio(Double coordLat, Double coordLng, String ciutat, String pais)
    {
        this.coordLat   = coordLat;
        this.coordLng   = coordLng;
        this.ciutat     = ciutat;
        this.pais       = pais;
    }

    public Double getCoordLat()
    {
        return coordLat;
    }

    public Double getCoordLng()
    {
        return coordLng;
    }

    public String getCiutat()
    {
        return ciutat;
    }

    public String getPais()
    {
        return pais;
    }
}
