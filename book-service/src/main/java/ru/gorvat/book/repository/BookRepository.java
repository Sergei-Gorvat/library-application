package ru.gorvat.book.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.gorvat.book.entity.Book;

public interface BookRepository extends CrudRepository <Book, Integer> {

    @Query (name = "Book.findAllByTitleLikeIgnoredCase", nativeQuery = true)
    Iterable <Book> findAllByTitleLikeIgnoreCase (@Param ("filter") String filter); // select * from catalogue.t_book where c_title ilike : filter
}
