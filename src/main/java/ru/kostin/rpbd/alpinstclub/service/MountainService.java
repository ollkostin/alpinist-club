package ru.kostin.rpbd.alpinstclub.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kostin.rpbd.alpinstclub.persistence.model.Mountain;
import ru.kostin.rpbd.alpinstclub.persistence.model.Route;
import ru.kostin.rpbd.alpinstclub.persistence.repository.MountainRepository;
import ru.kostin.rpbd.alpinstclub.persistence.repository.RouteRepository;
import ru.kostin.rpbd.alpinstclub.service.dto.MountainDTO;
import ru.kostin.rpbd.alpinstclub.service.dto.RouteDTO;
import ru.kostin.rpbd.alpinstclub.util.Search;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MountainService {
    private MountainRepository mountainRepository;
    private RouteRepository routeRepository;

    @Transactional
    public List<RouteDTO> findAllRoutesByMountain(String mountainName) {
        Mountain m = mountainRepository.findByName(mountainName);
        return routeRepository.findByMountain(m)
                .stream()
                .map(RouteDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<MountainDTO> fetchMountains() {
        return mountainRepository.findAll()
                .stream()
                .map(MountainDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public Mountain saveMountain(MountainDTO dto) {
        Mountain m = dto.getId() == null ? new Mountain() : mountainRepository.findOne(dto.getId());
        m.setName(dto.getName());
        m.setHeight(dto.getHeight());
        m.setLat(dto.getLat());
        m.setLon(dto.getLon());
        return mountainRepository.save(m);
    }

    @Transactional
    public List<MountainDTO> findAllByHeightCompare(Float height, Search search) {
        assert (search.equals(Search.LESS) || search.equals(Search.GREATER)
                || search.equals(Search.EQUALS));
        List<Mountain> mountains = new ArrayList<>();
        switch (search) {
            case LESS:
                mountains = mountainRepository.findAllByHeightLessThan(height);
                break;
            case GREATER:
                mountains = mountainRepository.findAllByHeightGreaterThan(height);
                break;
            case EQUALS:
                mountains = mountainRepository.findAllByHeightEquals(height);
                break;
        }
        return mountains
                .stream()
                .map(MountainDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteMountain(Integer id) {
        mountainRepository.delete(id);
    }

    @Transactional
    public void deleteRoute(Integer id) {
        routeRepository.delete(id);
    }

    @Transactional
    public Route saveRoute(RouteDTO dto) {
        Route r = dto.getId() != null ? routeRepository.findOne(dto.getId()) : new Route() ;
        r.setName(dto.getName());
        r.setMountain(mountainRepository.findOne(dto.getMountainId()));
        return routeRepository.save(r);
    }

    @Transactional
    public List<MountainDTO> findAllByNameLike(String name) {
        return mountainRepository.findAllByNameIsLike(name)
                .stream()
                .map(MountainDTO::new)
                .collect(Collectors.toList());
    }

    @Autowired
    public MountainService(MountainRepository mountainRepository, RouteRepository routeRepository) {
        this.mountainRepository = mountainRepository;
        this.routeRepository = routeRepository;
    }
}
