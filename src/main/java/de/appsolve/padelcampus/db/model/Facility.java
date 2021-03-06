/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.appsolve.padelcampus.db.model;

import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import java.util.Set;

/**
 * @author dominik
 */
@Entity
public class Facility extends CustomerEntity {

    @Transient
    private static final long serialVersionUID = 1L;

    @Column
    @NotEmpty(message = "{NotEmpty.name}")
    private String name;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Offer> offers;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Offer> getOffers() {
        return offers;
    }

    public void setOffers(Set<Offer> offers) {
        this.offers = offers;
    }

    @Override
    public String toString() {
        return name;
    }
}