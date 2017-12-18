package ru.kostin.rpbd.alpinstclub.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kostin.rpbd.alpinstclub.exception.UsernameAlreadyExistsException;
import ru.kostin.rpbd.alpinstclub.persistence.model.Person;
import ru.kostin.rpbd.alpinstclub.persistence.model.PersonLevel;
import ru.kostin.rpbd.alpinstclub.persistence.repository.PersonRepository;
import ru.kostin.rpbd.alpinstclub.service.dto.PersonDTO;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PersonService {
    private PersonRepository personRepository;

    @Transactional
    public Person edit(PersonDTO dto) throws UsernameAlreadyExistsException {
        Person p = personRepository.findByUsername(dto.getUsername());
        if (p != null && !p.getId().equals(dto.getId())) {
            throw new UsernameAlreadyExistsException("Пользователь с таким именем уже существует");
        }
        p = personRepository.findOne(dto.getId());
        p.setFullName(dto.getFullName());
        p.setUsername(dto.getUsername());
        p.setLevel(PersonLevel.valueOf(dto.getLevel()));
        return personRepository.save(p);
    }


    @Transactional
    public Person getPerson(Integer id) {
        return personRepository.findOne(id);
    }

    @Transactional
    public List<PersonDTO> fetch() {
        return personRepository.findAll().stream()
                .map(PersonDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<PersonDTO> filteredFetch(List<PersonLevel> levels, String query, String attr) {
        return personRepository.findAll((root, cq, cb) -> filterPredicate(root, cq, cb, levels, query, attr))
                .stream()
                .map(PersonDTO::new)
                .collect(Collectors.toList());
    }


    private Predicate filterPredicate(Root<Person> root, CriteriaQuery<?> cq, CriteriaBuilder cb, List<PersonLevel> levels, String query, String attr) {
        Predicate p = root.get("level").in(levels);
        if (query != null && attr != null && !query.isEmpty() && !attr.isEmpty()) {
            return cb.and(cb.like(cb.lower(root.get(attr)), "%" + query.toLowerCase() + "%"), p);
        } else {
            return p;
        }
    }

    @Transactional
    public void deletePerson(Integer id) {
        personRepository.delete(id);
    }

    @Autowired
    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

}
