package ru.gorvat.book.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.gorvat.book.controller.payload.UpdateBookPayload;
import ru.gorvat.book.entity.Book;
import ru.gorvat.book.service.BookService;

import java.util.Locale;
import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
@RequestMapping ("book-api/books/{bookId:\\d+}")
public class BookRestController {

    private final BookService bookService;

    private final MessageSource messageSource;

    @ModelAttribute ("book")
    public Book getBook (@PathVariable ("bookId") int bookId) {
        return this.bookService.findBook (bookId).orElseThrow (() ->
                new NoSuchElementException ("errors.book.not_found"));
    }

    @GetMapping
    public Book findBook (@ModelAttribute ("book") Book book) {
        return book;
    }

    @PatchMapping
    public ResponseEntity <?> updateBook (@PathVariable ("bookId") int bookId,
                                          @Valid @RequestBody UpdateBookPayload payload,
                                          BindingResult bindingResult) throws BindException {
        if (bindingResult.hasErrors ()) {
            if (bindingResult instanceof BindException exception) {
                throw exception;
            } else {
                throw new BindException (bindingResult);
            }
        } else {
            this.bookService.updateBook (bookId, payload.title (), payload.author (), payload.publication ());
            return ResponseEntity.noContent ().build ();
        }
    }

    @DeleteMapping
    public ResponseEntity <Void> deleteBook (@PathVariable ("bookId") int bookId) {
        this.bookService.deleteBook (bookId);
        return ResponseEntity.noContent ().build ();
    }

    @ExceptionHandler (NoSuchElementException.class)
    public ResponseEntity <ProblemDetail> handlerNoSuchElementException (NoSuchElementException exception,
                                                                         Locale locale) {
        return ResponseEntity.status (HttpStatus.NOT_FOUND).
                body (ProblemDetail.forStatusAndDetail (HttpStatus.NOT_FOUND,
                this.messageSource.getMessage (exception.getMessage (), new Object[0],
                        exception.getMessage (), locale)));
    }
}
