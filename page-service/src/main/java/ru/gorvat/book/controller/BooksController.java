package ru.gorvat.book.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.gorvat.book.client.BadRequestException;
import ru.gorvat.book.client.BookRestClient;
import ru.gorvat.book.controller.payload.NewBookPayload;
import ru.gorvat.book.entity.Book;

@Controller
@RequiredArgsConstructor
@RequestMapping ("catalogue/books")
public class BooksController {

    private final BookRestClient restClient;

    @GetMapping ("list")
    public String getBooksList (Model model, @RequestParam (name = "filter", required = false) String filter) {
        model.addAttribute ("books", this.restClient.findAllBooks (filter));
        model.addAttribute ("filter", filter);
        return "catalogue/books/list";
    }

    @GetMapping ("create")
    public String getNewBook ( ) {
        return "catalogue/books/new_book";
    }

    @PostMapping ("create")
    public String createBook (NewBookPayload payload, Model model) {
        try {
            Book book = this.restClient.createBook (payload.title ( ), payload.author ( ), payload.publication ( ));
            return "redirect:/catalogue/books/%d".formatted (book.id ( ));
        } catch (BadRequestException exception) {
            model.addAttribute ("payload", payload);
            model.addAttribute ("errors", exception.getErrors ( ));
            return "catalogue/books/new_book";
        }
    }
}
