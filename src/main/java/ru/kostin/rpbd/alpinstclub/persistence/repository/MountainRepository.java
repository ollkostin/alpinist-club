package ru.kostin.rpbd.alpinstclub.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.kostin.rpbd.alpinstclub.persistence.model.Mountain;

import java.util.List;

public interface MountainRepository extends JpaRepository<Mountain, Integer> {
    Mountain findByName(String name);

    @Query("select m from Mountain m where lower(m.name) like concat('%',lower(:name),'%')")
    List<Mountain> findAllByNameIsLike(@Param("name") String name);

    List<Mountain> findAllByHeightLessThan(Float height);

    List<Mountain> findAllByHeightGreaterThan(Float height);

    List<Mountain> findAllByHeightEquals(Float height);

}
