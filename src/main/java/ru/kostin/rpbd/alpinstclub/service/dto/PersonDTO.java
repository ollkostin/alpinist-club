package ru.kostin.rpbd.alpinstclub.service.dto;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import ru.kostin.rpbd.alpinstclub.persistence.model.Person;

import java.util.Objects;

public class PersonDTO {
    private Integer id;
    private SimpleStringProperty username = new SimpleStringProperty();
    private SimpleStringProperty fullName = new SimpleStringProperty();
    private SimpleStringProperty level = new SimpleStringProperty();
    private SimpleBooleanProperty inTeam = new SimpleBooleanProperty();

    public PersonDTO() {
    }

    public PersonDTO(Person person) {
        this.id = person.getId();
        this.username = new SimpleStringProperty(person.getUsername());
        this.fullName = new SimpleStringProperty(person.getFullName());
        this.level = new SimpleStringProperty(person.getLevel().name());
    }

    public Integer getId() {
        return id;
    }

    public String getUsername() {
        return username.get();
    }

    public SimpleStringProperty usernameProperty() {
        return username;
    }

    public String getFullName() {
        return fullName.get();
    }

    public SimpleStringProperty fullNameProperty() {
        return fullName;
    }

    public String getLevel() {
        return level.get();
    }

    public SimpleStringProperty levelProperty() {
        return level;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username.set(username);
    }

    public void setFullName(String fullName) {
        this.fullName.set(fullName);
    }

    public void setLevel(String level) {
        this.level.set(level);
    }

    public boolean isInTeam() {
        return inTeam.get();
    }

    public SimpleBooleanProperty inTeamProperty() {
        return inTeam;
    }

    public void setInTeam(boolean inTeam) {
        this.inTeam.set(inTeam);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PersonDTO personDTO = (PersonDTO) o;
        return getId() == personDTO.getId() &&
                Objects.equals(getUsername(), personDTO.getUsername()) &&
                Objects.equals(getFullName(), personDTO.getFullName()) &&
                Objects.equals(getLevel(), personDTO.getLevel());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getId(), getUsername(), getFullName(), getLevel());
    }
}
