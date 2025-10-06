package ru.school21.TicTacToe.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.school21.TicTacToe.domain.model.Person;
import ru.school21.TicTacToe.domain.repository.PersonRepository;
import ru.school21.TicTacToe.security.PersonDetails;

import java.security.Principal;
import java.util.Optional;
import java.util.UUID;

@Service
public class PersonDetailsService implements UserDetailsService {
    private final PersonRepository personRepository;

    @Autowired
    public PersonDetailsService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Person> person = personRepository.findByUsername(username);
        if(person.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }
        return new PersonDetails(person.get());
    }

    public Person getCurrentPerson(Principal principal) {
        if(principal == null) {
            return null;
        }
        String username = principal.getName();
        Optional<Person> person = personRepository.findByUsername(username);
        if(person.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }
        return person.get();
    }

    public Person getPerson(UUID id) {
        Optional<Person> person = personRepository.findById(id);
        if(person.isEmpty()) {
            throw new UsernameNotFoundException("Person not found");
        }
        return person.get();
    }
}
