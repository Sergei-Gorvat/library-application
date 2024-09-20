package ru.gorvat.book.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import ru.gorvat.book.controller.payload.NewBookPayload;
import ru.gorvat.book.entity.Book;
import ru.gorvat.book.service.BookService;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping ("book-api/books")
public class BooksRestController {

    private final BookService bookService;

    @GetMapping
    Iterable <Book> findBooks (@RequestParam (name = "filter", required = false) String filter) {
        LoggerFactory.getLogger (BooksRestController.class);
        return this.bookService.findAllBooks (filter);
    }

    @PostMapping
    public ResponseEntity <?> createBook (@Valid @RequestBody NewBookPayload payload, BindingResult bindingResult, UriComponentsBuilder uriComponentsBuilder) throws BindException {
        if (bindingResult.hasErrors ()) {
            if (bindingResult instanceof BindException exception) {
                throw exception;
            } else {
                throw new BindException (bindingResult);
            }
        } else {
            Book book = this.bookService.createBook (payload.title (), payload.author (), payload.publication ());
            return ResponseEntity.created (uriComponentsBuilder.replacePath ("book-api/books/{bookId}").build (Map.of ("bookId", book.getId ()))).body (book);
        }
    }
}
