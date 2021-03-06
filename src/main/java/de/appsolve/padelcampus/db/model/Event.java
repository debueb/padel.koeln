/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.appsolve.padelcampus.db.model;

import de.appsolve.padelcampus.constants.Currency;
import de.appsolve.padelcampus.constants.EventType;
import de.appsolve.padelcampus.constants.Gender;
import de.appsolve.padelcampus.constants.PaymentMethod;
import org.hibernate.annotations.SortNatural;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import static de.appsolve.padelcampus.utils.FormatUtils.TIME_HUMAN_READABLE;

/**
 * @author dominik
 */
@Entity
public class Event extends ComparableEntity {

    @Transient
    private static final long serialVersionUID = 1L;

    @Column
    @NotEmpty(message = "{NotEmpty.eventName}")
    private String name;

    @Column
    @Enumerated(EnumType.STRING)
    private EventType eventType;

    @Column
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column
    private Integer numberOfGroups;

    @Column
    private Integer numberOfGroupsSecondRound;

    @Column
    private Integer numberOfWinnersPerGroup;

    @Column
    private Integer numberOfSets;

    @Column
    private Integer numberOfGamesPerSet;

    @Column
    private Integer numberOfGamesInFinalSet;

    @Column
    private Boolean active;

    @ManyToMany(fetch = FetchType.LAZY)
    @OrderBy("firstName, lastName")
    private Set<Participant> participants;

    @Column
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate startDate;

    @Column
    private Integer startTimeHour;

    @Column
    private Integer startTimeMinute;

    @Column
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate endDate;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "event", cascade = CascadeType.REMOVE)
    @OrderBy(value = "id")
    private Set<Game> games;

    @Column
    private String location;

    @Column(columnDefinition = "text")
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    private EventGroup eventGroup;

    @Column
    @NotNull(message = "{NotEmpty.maxNumberOfParticipants}")
    private Integer maxNumberOfParticipants;

    @Column
    private BigDecimal price;

    @Column
    @Enumerated(EnumType.STRING)
    private Currency currency;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @SortNatural
    private SortedSet<PaymentMethod> paymentMethods;

    @Column
    private Boolean allowSignup;

    @Column
    private Boolean showParticipants;

    @Column
    private Boolean showGames;

    @Column
    private Boolean showScores;

    @Column
    private Boolean showEventType;

    @Column(length = 8000)
    private String confirmationMailRemark;

    @OneToMany(fetch = FetchType.LAZY)
    private Set<Community> communities;

    @Column
    private Boolean showInEventOverview;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public EventType getEventType() {
        return eventType == null ? EventType.SingleRoundRobin : eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public Gender getGender() {
        return gender == null ? Gender.male : gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Integer getNumberOfGroups() {
        return numberOfGroups == null ? 2 : numberOfGroups;
    }

    public void setNumberOfGroups(Integer numberOfGroups) {
        this.numberOfGroups = numberOfGroups;
    }

    public Integer getNumberOfGroupsSecondRound() {
        return numberOfGroupsSecondRound == null ? 2 : numberOfGroupsSecondRound;
    }

    public void setNumberOfGroupsSecondRound(Integer numberOfGroupsSecondRound) {
        this.numberOfGroupsSecondRound = numberOfGroupsSecondRound;
    }

    public Integer getNumberOfWinnersPerGroup() {
        return numberOfWinnersPerGroup == null ? 2 : numberOfWinnersPerGroup;
    }

    public void setNumberOfWinnersPerGroup(Integer numberOfWinnersPerGroup) {
        this.numberOfWinnersPerGroup = numberOfWinnersPerGroup;
    }

    public Integer getNumberOfSets() {
        return numberOfSets == null ? 3 : Math.max(1, numberOfSets);
    }

    public void setNumberOfSets(Integer numberOfSets) {
        this.numberOfSets = numberOfSets;
    }

    public Integer getNumberOfGamesPerSet() {
        return numberOfGamesPerSet == null ? 7 : Math.max(1, numberOfGamesPerSet);
    }

    public void setNumberOfGamesPerSet(Integer numberOfGamesPerSet) {
        this.numberOfGamesPerSet = numberOfGamesPerSet;
    }

    public Integer getNumberOfGamesInFinalSet() {
        return numberOfGamesInFinalSet == null ? 1 : Math.max(1, numberOfGamesInFinalSet);
    }

    public void setNumberOfGamesInFinalSet(Integer numberOfGamesInFinalSet) {
        this.numberOfGamesInFinalSet = numberOfGamesInFinalSet;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Set<Participant> getParticipants() {
        return participants == null ? new LinkedHashSet<>() : participants;
    }

    public void setParticipants(Set<Participant> participant) {
        this.participants = participant;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate dateTime) {
        this.startDate = dateTime;
    }

    public Integer getStartTimeHour() {
        return startTimeHour;
    }

    public void setStartTimeHour(Integer startTimeHour) {
        this.startTimeHour = startTimeHour;
    }

    public Integer getStartTimeMinute() {
        return startTimeMinute;
    }

    public void setStartTimeMinute(Integer startTimeMinute) {
        this.startTimeMinute = startTimeMinute;
    }

    public LocalTime getStartTime() {
        if (getStartTimeHour() == null || getStartTimeMinute() == null) {
            return null;
        }
        return TIME_HUMAN_READABLE.parseLocalTime(getStartTimeHour() + ":" + getStartTimeMinute());
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Set<Game> getGames() {
        return games;
    }

    public void setGames(Set<Game> games) {
        this.games = games;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public EventGroup getEventGroup() {
        return eventGroup;
    }

    public void setEventGroup(EventGroup eventGroup) {
        this.eventGroup = eventGroup;
    }

    public Integer getMaxNumberOfParticipants() {
        return maxNumberOfParticipants == null ? 0 : maxNumberOfParticipants;
    }

    public void setMaxNumberOfParticipants(Integer maxNumberOfParticipants) {
        this.maxNumberOfParticipants = maxNumberOfParticipants;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Set<PaymentMethod> getPaymentMethods() {
        return paymentMethods;
    }

    public void setPaymentMethods(SortedSet<PaymentMethod> paymentMethods) {
        this.paymentMethods = paymentMethods;
    }

    public Boolean getAllowSignup() {
        return allowSignup == null ? Boolean.FALSE : allowSignup;
    }

    public void setAllowSignup(Boolean allowSignup) {
        this.allowSignup = allowSignup;
    }

    public Boolean getShowParticipants() {
        return showParticipants == null ? Boolean.TRUE : showParticipants;
    }

    public void setShowParticipants(Boolean showParticipants) {
        this.showParticipants = showParticipants;
    }

    public Boolean getShowGames() {
        return showGames == null ? Boolean.TRUE : showGames;
    }

    public void setShowGames(Boolean showGames) {
        this.showGames = showGames;
    }

    public Boolean getShowScores() {
        return showScores == null ? Boolean.TRUE : showScores;
    }

    public void setShowScores(Boolean showScores) {
        this.showScores = showScores;
    }

    public Boolean getShowEventType() {
        return showEventType == null ? Boolean.TRUE : showEventType;
    }

    public void setShowEventType(Boolean showEventType) {
        this.showEventType = showEventType;
    }

    public String getConfirmationMailRemark() {
        return confirmationMailRemark;
    }

    public void setConfirmationMailRemark(String confirmationMailRemark) {
        this.confirmationMailRemark = confirmationMailRemark;
    }

    public Boolean getShowInEventOverview() {
        return showInEventOverview == null ? Boolean.TRUE : showInEventOverview;
    }

    public void setShowInEventOverview(Boolean showInEventOverview) {
        this.showInEventOverview = showInEventOverview;
    }

    public Set<Community> getCommunities() {
        return communities == null ? new TreeSet<>() : communities;
    }

    public void setCommunities(Set<Community> communities) {
        this.communities = communities;
    }

    public Set<Team> getTeams() {
        Set<Team> teams = new TreeSet<>();
        for (Participant participant : getParticipants()) {
            if (participant instanceof Team) {
                teams.add((Team) participant);
            }
        }
        return teams;
    }

    public Set<Player> getPlayers() {
        Set<Player> players = new TreeSet<>();
        for (Participant participant : getParticipants()) {
            if (participant instanceof Player) {
                players.add((Player) participant);
            }
        }
        return players;
    }

    public Set<Player> getAllPlayers() {
        Set<Player> players = new TreeSet<>();
        for (Participant participant : getParticipants()) {
            if (participant instanceof Player) {
                players.add((Player) participant);
            } else if (participant instanceof Team) {
                Team team = (Team) participant;
                players.addAll(team.getPlayers());
            }
        }
        return players;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int compareTo(BaseEntityI o) {
        if (o instanceof Event) {
            Event other = (Event) o;
            return getName().compareToIgnoreCase(other.getName());
        }
        return super.compareTo(o);
    }
}