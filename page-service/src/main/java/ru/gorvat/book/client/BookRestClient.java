package ru.gorvat.book.client;

import ru.gorvat.book.entity.Book;

import java.util.List;
import java.util.Optional;

public interface BookRestClient {

    List <Book> findAllBooks (String filter);

    Book createBook (String title, String author, int publication);

    Optional <Book> findBook (int bookId);

    void updateBook (int bookId, String title, String author, int publication);

    void deleteBook (int bookId);


}
