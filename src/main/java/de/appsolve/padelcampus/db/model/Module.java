/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.appsolve.padelcampus.db.model;

import de.appsolve.padelcampus.constants.ModuleType;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.validation.constraints.Pattern;
import org.hibernate.annotations.SortNatural;
import org.hibernate.validator.constraints.NotEmpty;

/**
 *
 * @author dominik
 */
@Entity
public class Module extends SortableEntity{
    
    @Transient
    private static final long serialVersionUID = 1L;
    
    @Column
    @Enumerated(EnumType.STRING)
    private ModuleType moduleType;
    
    @Column
    private String iconName;
    
    @Column
    @NotEmpty(message = "{NotEmpty.title}")
    @Pattern(regexp = "^[öÖäÄüÜßa-zA-Z0-9\\s]*$", message = "{RegExp.AlphaNum}") 
    private String title;
    
    @Column
    private Boolean showInMenu;
    
    @Column
    private Boolean showInFooter;
    
    @Column
    private Boolean showOnHomepage;
    
    @OneToMany(fetch = FetchType.EAGER)
    private Set<EventGroup> eventGroups;
    
    @Column
    private String shortDescription;
    
    @Column(length = 8000)
    private String description;
    
    @Column
    private String url;
    
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @SortNatural
    private Set<Module> subModules;
    
    public ModuleType getModuleType() {
        return moduleType;
    }

    public void setModuleType(ModuleType moduleType) {
        this.moduleType = moduleType;
    }

    public String getIconName() {
        return iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }
    
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getShowOnHomepage() {
        return showOnHomepage == null ? Boolean.TRUE : showOnHomepage;
    }

    public void setShowOnHomepage(Boolean showOnHomepage) {
        this.showOnHomepage = showOnHomepage;
    }

    public Boolean getShowInMenu() {
        return showInMenu == null ? Boolean.TRUE : showInMenu;
    }

    public void setShowInMenu(Boolean showInMenu) {
        this.showInMenu = showInMenu;
    }

    public Boolean getShowInFooter() {
        return showInFooter == null ? Boolean.TRUE : showInFooter;
    }

    public void setShowInFooter(Boolean showInFooter) {
        this.showInFooter = showInFooter;
    }

    public Set<EventGroup> getEventGroups() {
        return eventGroups;
    }

    public void setEventGroups(Set<EventGroup> eventGroups) {
        this.eventGroups = eventGroups;
    }
    
    public String getUrl(){
        if (moduleType == null){
            return null;
        }
        switch (moduleType){
            case LandingPage:
                return "/";
            case HomePage:
                return "/home";
            case Bookings:
                return "/bookings";
            case MatchOffers:
                return "/matchoffers";
            case Staff:
                return "/staff";
            case Ranking:
                return "/ranking";
            case Link:
                return url;
            default:
                String moduleName = "/" + moduleType.name().toLowerCase();
                moduleName += "/" + getTitle();
                moduleName = moduleName.replace(" ", "-");
                return moduleName;
        }
        
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Module> getSubModules() {
        return subModules;
    }

    public void setSubModules(Set<Module> subModules) {
        this.subModules = subModules;
    }

    @Override
    public String toString() {
        return title;
    }
}