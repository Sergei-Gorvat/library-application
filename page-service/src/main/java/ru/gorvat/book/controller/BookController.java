package ru.gorvat.book.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.gorvat.book.client.BadRequestException;
import ru.gorvat.book.client.BookRestClient;
import ru.gorvat.book.controller.payload.UpdateBookPayload;
import ru.gorvat.book.entity.Book;

import java.util.Locale;
import java.util.NoSuchElementException;

@Controller
@RequestMapping ("catalogue/books/{bookId:\\d+}")
@RequiredArgsConstructor
public class BookController {

    private final BookRestClient restClient;

    private final MessageSource messageSource;

    @ModelAttribute ("book")
    public Book book (@PathVariable ("bookId") int bookId) {
        return this.restClient.findBook (bookId).orElseThrow (() -> new NoSuchElementException ("errors.book.not_found"));
    }

    @GetMapping
    public String getBook () {
        return "catalogue/books/book";
    }

    @GetMapping ("edit")
    public String getBookEditPage () {
        return "catalogue/books/edit";
    }

    @PostMapping ("edit")
    public String updateBook (@ModelAttribute (value = "book", binding = false) Book book, UpdateBookPayload payload, Model model) {
        try {
            this.restClient.updateBook (book.id (), payload.title (), payload.author (), payload.publication ());
            return "redirect:/catalogue/books/%d".formatted (book.id ());
        } catch (BadRequestException exception) {
            model.addAttribute ("payload", payload);
            model.addAttribute ("errors", exception.getErrors ());
            return "catalogue/books/edit";
        }
    }

    @PostMapping ("delete")
    public String deleteBook (@ModelAttribute ("book") Book book) {
        this.restClient.deleteBook (book.id ());
        return "redirect:/catalogue/books/list";
    }

    @ExceptionHandler (NoSuchElementException.class)
    public String handlerNoSuchElementException (NoSuchElementException exception, Model model, HttpServletResponse response, Locale locale) {
        response.setStatus (HttpStatus.NOT_FOUND.value ());
        model.addAttribute ("error", this.messageSource.getMessage (exception.getMessage (), new Object[0], exception.getMessage (), locale));
        return "errors/404";
    }
}
