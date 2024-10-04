package ru.gorvat.book.repository;

import org.springframework.data.repository.CrudRepository;
import ru.gorvat.book.entity.Book;

public interface BookRepository extends CrudRepository <Book, Integer> {

    Iterable <Book> findAllByTitleLikeIgnoreCase (String filter); // select * from catalogue.t_book where c_title ilike : filter
}
