package ru.kostin.rpbd.alpinstclub.persistence.repository;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.kostin.rpbd.alpinstclub.persistence.model.Climbing;
import ru.kostin.rpbd.alpinstclub.persistence.model.PersonLevel;

import java.util.List;

public interface ClimbingRepository extends JpaRepository<Climbing, Integer>, JpaSpecificationExecutor<Climbing> {
    List<Climbing> findAllByMinLevelIn(List<PersonLevel> levels);
    List<Climbing> findAllByMinLevelIn(List<PersonLevel> levels, Specification<Climbing> spec);
}
