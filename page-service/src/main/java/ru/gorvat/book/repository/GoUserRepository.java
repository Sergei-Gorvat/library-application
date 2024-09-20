package ru.gorvat.book.repository;

import org.springframework.data.repository.CrudRepository;
import ru.gorvat.book.entity.GoUser;

import java.util.Optional;

public interface GoUserRepository extends CrudRepository <GoUser, Integer> {

    Optional <GoUser> findByUsername (String username);
}
