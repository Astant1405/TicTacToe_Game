package ru.school21.TicTacToe.domain.repository;

import org.springframework.data.repository.CrudRepository;
import ru.school21.TicTacToe.domain.model.Person;

import java.util.Optional;
import java.util.UUID;

public interface PersonRepository extends CrudRepository<Person, UUID> {
    Optional<Person> findByUsername(String username);
    Optional<Person> findById(UUID id);
}
