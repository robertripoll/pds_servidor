package org.udg.pds.cheapy.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.udg.pds.cheapy.rest.serializer.JsonDateDeserializer;
import org.udg.pds.cheapy.rest.serializer.JsonDateSerializer;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
public class Missatge implements Serializable, Cloneable
{
    public enum Estat {
        PENDENT_ENVIAMENT("pendent"), ENVIAT("enviat"), LLEGIT("llegit");

        private String value;

        private Estat(String value) {
            this.value = value;
        }

        @JsonValue
        public String getValue() {
            return this.value;
        }

        @JsonCreator
        public static Estat create(String val)
        {
            Estat[] estats = Estat.values();

            for (Estat estat : estats)
            {
                if (estat.getValue().equalsIgnoreCase(val))
                    return estat;
            }

            return PENDENT_ENVIAMENT;
        }
    }

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.Private.class)
    protected Long id;

    @JsonView(Views.Private.class)
    @ManyToOne
    private Conversacio conversacio;

    @ManyToOne
    private User emisor;

    @ManyToOne
    private User receptor;

    @Basic
    @Enumerated(EnumType.STRING)
    @NotNull
    @JsonView(Views.Public.class)
    private Estat estat;

    @NotNull
    private String missatge;

    @Column(nullable = false, updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @JsonView(Views.Public.class)
    @JsonSerialize(using = JsonDateSerializer.class)
    @JsonDeserialize(using = JsonDateDeserializer.class)
    private java.sql.Timestamp dataEnviament;

    public Missatge()
    {

    }

    public Missatge(Conversacio c, User emisor, User receptor, String missatge)
    {
        this.conversacio    = c;
        this.emisor         = emisor;
        this.receptor       = receptor;
        this.missatge       = missatge;
        this.estat          = Estat.PENDENT_ENVIAMENT;
    }

    private Missatge(Conversacio c, User emisor, User receptor, String missatge, Estat estat)
    {
        this.conversacio    = c;
        this.emisor         = emisor;
        this.receptor       = receptor;
        this.missatge       = missatge;
        this.estat          = estat;
    }

    public Conversacio getConversacio()
    {
        return conversacio;
    }

    public User getEmisor()
    {
        return emisor;
    }

    public User getReceptor()
    {
        return receptor;
    }

    public String getMissatge()
    {
        return missatge;
    }

    public Estat getEstat()
    {
        return estat;
    }

    public java.sql.Timestamp getDataEnviament()
    {
        return dataEnviament;
    }

    public void setEstat(Estat nouEstat)
    {
        estat = nouEstat;
    }

    public Missatge clone(Conversacio c)
    {
        return new Missatge(c, this.emisor, this.receptor, this.missatge, this.estat);
    }
}
