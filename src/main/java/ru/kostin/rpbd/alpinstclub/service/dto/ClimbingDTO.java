package ru.kostin.rpbd.alpinstclub.service.dto;

import javafx.beans.property.SimpleStringProperty;
import ru.kostin.rpbd.alpinstclub.persistence.model.Climbing;

import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

public class ClimbingDTO {
    private Integer id;
    private Date startDate;
    private Date endDate;
    private Integer personLimit;
    private SimpleStringProperty mountain;
    private SimpleStringProperty status;
    private SimpleStringProperty minLevel;
    private Set<PersonDTO> members;
    private RouteDTO route;
    private Boolean personIsMember = false;

    public ClimbingDTO() {
        this.mountain = new SimpleStringProperty();
        this.status = new SimpleStringProperty();
        this.minLevel = new SimpleStringProperty();
    }

    public ClimbingDTO(Climbing c) {
        this.id = c.getId();
        this.mountain = new SimpleStringProperty(c.getMountain().getName());
        this.status = new SimpleStringProperty(c.getStatus().name());
        this.startDate = c.getStartTime();
        this.endDate = c.getEndTime();
        this.minLevel = new SimpleStringProperty(c.getMinLevel().name());
        this.personLimit = c.getPersonLimit();
        this.members = c.getMembers().stream().map(PersonDTO::new).collect(Collectors.toSet());
        this.route = new RouteDTO(c.getRoute());
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getMountain() {
        return mountain.get();
    }

    public SimpleStringProperty mountainProperty() {
        return mountain;
    }

    public void setMountain(String mountain) {
        this.mountain.set(mountain);
    }

    public String getStatus() {
        return status.get();
    }

    public SimpleStringProperty statusProperty() {
        return status;
    }

    public void setStatus(String status) {
        this.status.set(status);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMinLevel() {
        return minLevel.get();
    }

    public SimpleStringProperty minLevelProperty() {
        return minLevel;
    }

    public void setMinLevel(String minLevel) {
        this.minLevel.set(minLevel);
    }

    public Integer getPersonLimit() {
        return personLimit;
    }

    public void setPersonLimit(Integer personLimit) {
        this.personLimit = personLimit;
    }

    public Set<PersonDTO> getMembers() {
        return members;
    }

    public void setMembers(Set<PersonDTO> members) {
        this.members = members;
    }

    public boolean getPersonIsMember() {
        return personIsMember;
    }

    public void setPersonIsMember(boolean personIsMember) {
        this.personIsMember = personIsMember;
    }

    public RouteDTO getRoute() {
        return route;
    }

    public void setRoute(RouteDTO route) {
        this.route = route;
    }
}
