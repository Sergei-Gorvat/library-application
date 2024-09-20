package ru.gorvat.customer.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;
import ru.gorvat.customer.client.BooksClient;
import ru.gorvat.customer.entity.Book;

@Controller
@RequiredArgsConstructor
@RequestMapping ("customer/books/{bookId:\\d+}")
public class BookController {

    private final BooksClient booksClient;

    @ModelAttribute (name = "book", binding = false)
    public Mono <Book> loadBook (@PathVariable ("bookId") int id) {
        return this.booksClient.findBook (id);
    }

    @GetMapping
    public String getBook () {
        return "customer/books/book";
    }
}
