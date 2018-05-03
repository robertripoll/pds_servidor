package org.udg.pds.cheapy.model;

import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;
import java.io.Serializable;

@Entity(name = "transaccions")
public class Transaccio implements Serializable
{
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.Private.class)
    protected Long id;

    @ManyToOne
    private User comprador;

    @ManyToOne(optional = false)
    private User venedor;

    @OneToOne
    private Valoracio valoracioComprador;

    @OneToOne
    private Valoracio valoracioVenedor;

    public Transaccio()
    {

    }

    public Transaccio(User venedor)
    {
        this.venedor = venedor;
    }

    public Transaccio(User venedor, User comprador, Valoracio valoracioVenedor)
    {
        this.venedor            = venedor;
        this.comprador          = comprador;
        this.valoracioVenedor   = valoracioVenedor;
    }

    public User getVenedor()
    {
        return venedor;
    }

    public User getComprador()
    {
        return comprador;
    }

    public Valoracio getValoracioComprador()
    {
        return valoracioComprador;
    }

    public Valoracio getValoracioVenedor()
    {
        return valoracioVenedor;
    }

    public void setValoracioComprador(Valoracio v)
    {
        valoracioComprador = v;
    }
}
