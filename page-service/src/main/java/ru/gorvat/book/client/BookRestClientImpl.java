package ru.gorvat.book.client;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import ru.gorvat.book.controller.payload.NewBookPayload;
import ru.gorvat.book.controller.payload.UpdateBookPayload;
import ru.gorvat.book.entity.Book;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RequiredArgsConstructor
public class BookRestClientImpl implements BookRestClient {

    private final RestClient restClient;

    private final static ParameterizedTypeReference <List <Book>> BOOKS_TYPE_REFERENCE =
            new ParameterizedTypeReference <> ( ) {
            };

    @Override
    public List <Book> findAllBooks (String filter) {
        return this.restClient
                .get ( )
                .uri ("/book-api/books?filter={filter}", filter)
                .retrieve ( )
                .body (BOOKS_TYPE_REFERENCE);
    }

    @Override
    public Book createBook (String title, String author, int publication) {
        try {
            return this.restClient
                    .post ( )
                    .uri ("/book-api/books")
                    .contentType (MediaType.APPLICATION_JSON)
                    .body (new NewBookPayload (title, author, publication))
                    .retrieve ( )
                    .body (Book.class);
        } catch (HttpClientErrorException.BadRequest exception) {
            ProblemDetail problemDetail = exception.getResponseBodyAs (ProblemDetail.class);
            throw new BadRequestException ((List <String>) problemDetail.getProperties ( ).get ("errors"));
        }
    }

    @Override
    public Optional <Book> findBook (int bookId) {
        try {
            return Optional.ofNullable (this.restClient
                    .get ( )
                    .uri ("/book-api/books/{bookId}", bookId)
                    .retrieve ( )
                    .body (Book.class));
        } catch (HttpClientErrorException.NotFound exception) {
            return Optional.empty ( );
        }
    }

    @Override
    public void updateBook (int bookId, String title, String author, int publication) {
        try {
            this.restClient
                    .patch ( )
                    .uri ("/book-api/books/{bookId}", bookId)
                    .contentType (MediaType.APPLICATION_JSON)
                    .body (new UpdateBookPayload (title, author, publication))
                    .retrieve ( )
                    .toBodilessEntity ( );
        } catch (HttpClientErrorException.BadRequest exception) {
            ProblemDetail problemDetail = exception.getResponseBodyAs (ProblemDetail.class);
            throw new BadRequestException ((List <String>) problemDetail.getProperties ( ).get ("errors"));
        }
    }

    @Override
    public void deleteBook (int bookId) {
        try {
            this.restClient
                    .delete ( )
                    .uri ("/book-api/books/{bookId}", bookId)
                    .retrieve ( )
                    .toBodilessEntity ( );
        } catch (HttpClientErrorException.NotFound exception) {
            throw new NoSuchElementException (exception);
        }
    }
}
