package org.udg.pds.cheapy.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

import org.joda.time.DateTime;

import javax.persistence.*;
import javax.swing.text.View;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity(name = "usuaris")
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"correu", "nom", "telefon"}))
public class User implements Serializable {
    /**
     * Default value included to remove warning. Remove or modify at will. *
     */
    private static final long serialVersionUID = 1L;

    public User(){
    }

    public User(String sexe, String nom, String cognoms, String telefon, DateTime dataNaix, String correu, String contrasenya, Ubicacio ubicacio){
        this.sexe = sexe;
        this.nom = nom;
        this.cognoms = cognoms;
        this.telefon = telefon;
        this.dataNaix = dataNaix;
        this.correu = correu;
        this.contrasenya = contrasenya;
        this.ubicacio = ubicacio;
        this.missatges = new ArrayList<>();
        this.converses = new ArrayList<>();
        this.valoracions = new ArrayList<>();
        this.compres = new ArrayList<>();
        this.vendes = new ArrayList<>();
        this.favorits = new ArrayList<>();
        this.prodVenda = new ArrayList<>();
    }

    public User(String nom, String correu, String contrasenya){
        this.nom = nom;
        this.correu = correu;
        this.contrasenya = contrasenya;
        this.missatges = new ArrayList<>();
        this.converses = new ArrayList<>();
        this.valoracions = new ArrayList<>();
        this.compres = new ArrayList<>();
        this.vendes = new ArrayList<>();
        this.favorits = new ArrayList<>();
        this.prodVenda = new ArrayList<>();
    }

    //-------------------- ATRIBUTS DE LA CLASSE --------------------//

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.Private.class)
    protected Long id;

    @NotNull
    @JsonIgnore
    private String contrasenya;

    @NotNull
    @JsonView(Views.Private.class)
    private String correu;

    @NotNull
    @JsonView(Views.Public.class)
    private String nom;

    @NotNull
    @JsonView(Views.Private.class)
    private String cognoms;

    @NotNull
    @JsonView(Views.Public.class)
    private String sexe;

    @NotNull
    @JsonView(Views.Private.class)
    private String telefon;

    @NotNull
    @JsonView(Views.Private.class)
    private DateTime dataNaix;

    @NotNull
    @JsonView(Views.Private.class)
    private Ubicacio ubicacio;

    //-------------------- ATRIBUTS AMB RELACIÓ AMB ALTRES ENTITATS --------------------//

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "usuari")
    @JsonView(Views.Complete.class)
    private Collection<Missatge> missatges;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "usuari")
    @JsonView(Views.Complete.class)
    private Collection<Conversacio> converses;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "usuari")
    @JsonView(Views.Complete.class)
    private Collection<Valoracio> valoracions;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "comprador")
    @JsonView(Views.Complete.class)
    private Collection<Transaccio> compres;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "venedor")
    @JsonView(Views.Complete.class)
    private Collection<Transaccio> vendes;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "usuari")
    @JsonView(Views.Complete.class)
    private Collection<Producte> favorits;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "usuari")
    @JsonView(Views.Complete.class)
    private Collection<Producte> prodVenda;

    //-------------------- GETTERS I SETTERS --------------------//

    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    public String getContrasenya(){
        return contrasenya;
    }

    public void setContrasenya(String contrasenya){
        this.contrasenya = contrasenya;
    }

    public String getCorreu(){
        return correu;
    }

    public void setCorreu(String correu){
        this.correu = correu;
    }

    public String getNom(){
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getCognoms(){
        return cognoms;
    }

    public void setCognoms(String cognoms){
        this.cognoms = cognoms;
    }

    public String getSexe(){
        return sexe;
    }

    public void setSexe(String sexe){
        this.sexe = sexe;
    }

    public String getTelefon(){
        return telefon;
    }

    public void setTelefon(String telefon){
        this.telefon = telefon;
    }

    public DateTime getDataNaix(){
        return dataNaix;
    }

    public void setDataNaix(DateTime dataNaix){
        this.dataNaix = dataNaix;
    }

    public Ubicacio getUbicacio() {
        return ubicacio;
    }

    public void setUbicacio(Ubicacio ubicacio){
        this.ubicacio = ubicacio;
    }

    //-------------------- OPERACIONS AMB LES COLECCIONS --------------------//

    public Collection<Missatge> getMissatges(){
        missatges.size();
        return missatges;
    }

    public void setMissatges(List<Missatge> miss){
        this.missatges = miss;
    }

    public void addMissatge(Missatge missatge){
        missatges.add(missatge);
    }

    public Collection<Conversacio> getConverses(){
        converses.size();
        return converses;
    }

    public void setConverses(List<Conversacio> cv){
        this.converses = cv;
    }

    public void addConversacio(Conversacio conversacio){
        converses.add(conversacio);
    }

    public Collection<Valoracio> getValoracions(){
        valoracions.size();
        return valoracions;
    }

    public void setValoracions(List<Valoracio> val){
        this.valoracions = val;
    }

    public void addValoracio(Valoracio valoracio){
        valoracions.add(valoracio);
    }

    public int getNumValoracions(){
        return valoracions.size();
    }

    public Collection<Transaccio> getCompres(){
        compres.size();
        return compres;
    }

    public void setCompres(List<Transaccio> comp){
        this.compres = comp;
    }

    public void addCompra(Transaccio compra){
        compres.add(compra);
    }

    public int getNumCompres(){
        return compres.size();
    }

    public Collection<Transaccio> getVendes(){
        vendes.size();
        return vendes;
    }

    public void setVendes(List<Transaccio> vend){
        this.vendes = vend;
    }

    public void addVenda(Transaccio venda){
        vendes.add(venda);
    }

    public int getNumVendes(){
        return vendes.size();
    }

    public Collection<Producte> getFavorits(){
        favorits.size();
        return favorits;
    }

    public void setFavorits(List<Producte> fav){
        this.favorits = fav;
    }

    public void addFavorit(Producte prod){
        favorits.add(prod);
    }

    public Collection<Producte> getProdVenda(){
        prodVenda.size();
        return prodVenda;
    }

    public void setProdVenda(List<Producte> prodV){
        this.prodVenda = prodV;
    }

    public void addProdVenda(Producte prod){
        prodVenda.add(prod);
    }
}
