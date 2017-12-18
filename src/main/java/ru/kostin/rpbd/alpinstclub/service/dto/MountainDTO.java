package ru.kostin.rpbd.alpinstclub.service.dto;

import javafx.beans.property.SimpleStringProperty;
import ru.kostin.rpbd.alpinstclub.persistence.model.Mountain;

import java.util.List;
import java.util.stream.Collectors;

public class MountainDTO {
    private Integer id;
    private SimpleStringProperty name;
    private Float height;
    private Float lat;
    private Float lon;
    private List<RouteDTO> routes;

    public MountainDTO() {
        this.name = new SimpleStringProperty();
    }

    public MountainDTO(Mountain mountain) {
        this.id = mountain.getId();
        this.name = new SimpleStringProperty(mountain.getName());
        this.height = mountain.getHeight();
        this.lat = mountain.getLat();
        this.lon = mountain.getLon();
        this.routes = mountain.getRoute()
                .stream()
                .map(RouteDTO::new)
                .collect(Collectors.toList());
    }

    public void setHeight(Float height) {
        this.height = height;
    }

    public void setLat(Float lat) {
        this.lat = lat;
    }

    public void setLon(Float lon) {
        this.lon = lon;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public Float getHeight() {
        return height;
    }

    public Float getLat() {
        return lat;
    }

    public Float getLon() {
        return lon;
    }

    public List<RouteDTO> getRoutes() {
        return routes;
    }

    public void setRoutes(List<RouteDTO> routes) {
        this.routes = routes;
    }
}
