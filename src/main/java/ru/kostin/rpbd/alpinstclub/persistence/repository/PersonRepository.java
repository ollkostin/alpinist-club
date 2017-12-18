package ru.kostin.rpbd.alpinstclub.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.kostin.rpbd.alpinstclub.persistence.model.Person;

public interface PersonRepository extends JpaRepository<Person, Integer>, JpaSpecificationExecutor<Person> {
    Person findByUsername(String username);

}