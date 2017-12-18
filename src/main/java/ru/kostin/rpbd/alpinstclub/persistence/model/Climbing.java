package ru.kostin.rpbd.alpinstclub.persistence.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "climbing")
public class Climbing implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Date startTime;
    private Date endTime;
    private Integer personLimit;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id")
    private Route route;
    @ManyToOne
    @JoinColumn(name = "mountain_id")
    private Mountain mountain;
    @Enumerated(EnumType.STRING)
    private ClimbingStatus status;
    @Enumerated(EnumType.STRING)
    private PersonLevel minLevel;
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
            name = "climbing_person",
            joinColumns = @JoinColumn(name = "climbing_id"),
            inverseJoinColumns = @JoinColumn(name = "person_id"))
    private Set<Person> members;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Mountain getMountain() {
        return mountain;
    }

    public void setMountain(Mountain mountain) {
        this.mountain = mountain;
    }

    public ClimbingStatus getStatus() {
        return status;
    }

    public void setStatus(ClimbingStatus status) {
        this.status = status;
    }

    public PersonLevel getMinLevel() {
        return minLevel;
    }

    public void setMinLevel(PersonLevel minLevel) {
        this.minLevel = minLevel;
    }

    public Integer getPersonLimit() {
        return personLimit;
    }

    public void setPersonLimit(Integer personLimit) {
        this.personLimit = personLimit;
    }

    public Set<Person> getMembers() {
        return members;
    }

    public void setMembers(Set<Person> members) {
        this.members = members;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }
}
