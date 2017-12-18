package ru.kostin.rpbd.alpinstclub.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kostin.rpbd.alpinstclub.persistence.model.Mountain;
import ru.kostin.rpbd.alpinstclub.persistence.model.Route;

import java.util.List;

public interface RouteRepository extends JpaRepository<Route, Integer> {
    List<Route> findByMountain(Mountain mountain);
    Route findByName(String name);
}
