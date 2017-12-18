package ru.kostin.rpbd.alpinstclub.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kostin.rpbd.alpinstclub.persistence.model.Person;
import ru.kostin.rpbd.alpinstclub.persistence.model.PersonLevel;
import ru.kostin.rpbd.alpinstclub.persistence.repository.PersonRepository;
import ru.kostin.rpbd.alpinstclub.util.CryptoUtil;

import javax.transaction.Transactional;

@Service
public class LoginService {
    private PersonRepository personRepository;

    @Transactional
    public Person login(String username, String password) {
        Person person = personRepository.findByUsername(username);
        if (person == null) {
            throw new IllegalArgumentException("Пользователя с таким именем не существует");
        }
        if (!person.getPasswordHash().equals(CryptoUtil.toSHA1(password))) {
            throw new IllegalArgumentException("Неверный пароль");
        }
        return person;
    }

    @Transactional
    public Person register(String username, String name, String password){
        Person person = personRepository.findByUsername(username);
        if (person != null){
            throw new IllegalArgumentException("Имя пользователя уже существует");
        }
        person = new Person();
        person.setUsername(username);
        person.setFullName(name);
        person.setPasswordHash(CryptoUtil.toSHA1(password));
        person.setLevel(PersonLevel.NEWBIE);
        return personRepository.save(person);
    }

    @Autowired
    public LoginService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }
}
