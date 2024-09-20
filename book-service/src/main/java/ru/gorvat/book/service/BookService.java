package ru.gorvat.book.service;

import ru.gorvat.book.entity.Book;

import java.util.Optional;

public interface BookService {

    Iterable <Book> findAllBooks (String filter);

    Book createBook (String title, String author, int publication);

    Optional <Book> findBook (int bookId);

    void updateBook (Integer id, String title, String author, int publication);

    void deleteBook (Integer id);
}
