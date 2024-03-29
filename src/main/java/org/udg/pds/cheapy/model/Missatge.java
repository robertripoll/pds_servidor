package org.udg.pds.cheapy.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.udg.pds.cheapy.rest.serializer.JsonDateTimeDeserializer;
import org.udg.pds.cheapy.rest.serializer.JsonDateTimeSerializer;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity(name = "missatges")
public class Missatge implements Serializable, Cloneable
{
    public enum Estat {
        ENVIAT("enviat"), LLEGIT("llegit");

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

            return ENVIAT;
        }
    }

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.Basic.class)
    protected Long id;

    @JsonIgnore
    @ManyToOne(optional = false)
    private Conversacio conversacio;

    @ManyToOne(optional = false)
    @JsonView(Views.Basic.class)
    private User emisor;

    @ManyToOne(optional = false)
    @JsonView(Views.Basic.class)
    private User receptor;

    @Basic
    @Enumerated(EnumType.STRING)
    @NotNull
    @JsonView(Views.Basic.class)
    private Estat estat;

    @NotNull
    @JsonView(Views.Basic.class)
    private String missatge;

    @Column(nullable = false, updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @JsonView(Views.Basic.class)
    @JsonSerialize(using = JsonDateTimeSerializer.class)
    @JsonDeserialize(using = JsonDateTimeDeserializer.class)
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
        this.estat          = Estat.ENVIAT;
    }

    private Missatge(Conversacio c, User emisor, User receptor, String missatge, Estat estat)
    {
        this.conversacio    = c;
        this.emisor         = emisor;
        this.receptor       = receptor;
        this.missatge       = missatge;
        this.estat          = estat;
    }

    public Long getId(){
        return id;
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

    @Override
    public boolean equals(Object o)
    {
        return ((Missatge)o).id.equals(this.id);
    }
}
