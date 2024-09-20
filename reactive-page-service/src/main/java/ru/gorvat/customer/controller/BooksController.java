package ru.gorvat.customer.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;
import ru.gorvat.customer.client.BooksClient;

@Controller
@RequiredArgsConstructor
@RequestMapping ("customer/books")
public class BooksController {

    private final BooksClient booksClient;

    @GetMapping ("list")
    public Mono <String> getBooksList (Model model, @RequestParam (name = "filter", required = false) String filter) {
        return this.booksClient.findAllBooks (filter)
                .collectList ()
                .doOnNext (books -> model.addAttribute ("books", books))
                .thenReturn ("customer/books/list");

    }
}

