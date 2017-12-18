package ru.kostin.rpbd.alpinstclub.service.dto;

import ru.kostin.rpbd.alpinstclub.persistence.model.Route;

public class RouteDTO {
    private Integer id;
    private String name;
    private Integer mountainId;

    public RouteDTO(){

    }

    public RouteDTO(Route r){
        this.id = r.getId();
        this.name = r.getName();
        this.mountainId = r.getMountain().getId();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getMountainId() {
        return mountainId;
    }

    public void setMountainId(Integer mountainId) {
        this.mountainId = mountainId;
    }
}
