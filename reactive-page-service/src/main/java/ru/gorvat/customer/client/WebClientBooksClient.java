package ru.gorvat.customer.client;

import lombok.RequiredArgsConstructor;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.gorvat.customer.entity.Book;

@RequiredArgsConstructor
public class WebClientBooksClient implements BooksClient {

    private final WebClient webClient;

    @Override
    public Flux <Book> findAllBooks (String filter) {
        return this.webClient.get ()
                .uri ("/book-api/books?filter=${filter}", filter)
                .retrieve ()
                .bodyToFlux (Book.class);
    }

    @Override
    public Mono <Book> findBook (int id) {
        return this.webClient.get ()
                .uri ("/book-api/book/{bookId}", id)
                .retrieve ()
                .bodyToMono (Book.class);
    }
}
