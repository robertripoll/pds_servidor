package org.udg.pds.cheapy.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
public class Valoracio implements Serializable
{
    public enum Estrelles {
        UNA(1), DUES(2), TRES(3), QUATRE(4), CINC(5);

        private int value;

        private Estrelles(int value) {
            this.value = value;
        }

        @JsonValue
        public int getValue() {
            return this.value;
        }

        @JsonCreator
        public static Estrelles create(int val)
        {
            Estrelles[] estrelles = Estrelles.values();

            for (Estrelles actual : estrelles)
            {
                if (actual.getValue() == val)
                    return actual;
            }

            return UNA;
        }
    }

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.Private.class)
    protected Long id;

    @Basic
    @Enumerated(EnumType.ORDINAL)
    @NotNull
    @JsonView(Views.Public.class)
    private Estrelles estrelles;

    @JsonView(Views.Private.class)
    private String comentaris;

    @ManyToOne
    @JsonView(Views.Complete.class)
    private User valorador;

    @ManyToOne
    @JsonView(Views.Complete.class)
    private User valorat;

    public Valoracio()
    {

    }

    public Valoracio(User valorador, User valorat, Estrelles estrelles)
    {
        this.valorador  = valorador;
        this.valorat    = valorat;
        this.estrelles  = estrelles;
    }

    public Valoracio(User valorador, User valorat, Estrelles estrelles, String comentaris)
    {
        this(valorador, valorat, estrelles);

        this.comentaris = comentaris;
    }

    public Estrelles getEstrelles()
    {
        return estrelles;
    }

    public String getComentaris()
    {
        return comentaris;
    }

    public User getValorador()
    {
        return valorador;
    }

    public User getValorat()
    {
        return valorat;
    }
}
