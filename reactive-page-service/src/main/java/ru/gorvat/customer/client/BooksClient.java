package ru.gorvat.customer.client;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.gorvat.customer.entity.Book;


public interface BooksClient {
    Flux <Book> findAllBooks (String filter);

    Mono<Book> findBook (int id);
}
