package ru.gorvat.book.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ConcurrentModel;
import ru.gorvat.book.client.BadRequestException;
import ru.gorvat.book.client.BookRestClient;
import ru.gorvat.book.controller.payload.NewBookPayload;
import ru.gorvat.book.entity.Book;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith (MockitoExtension.class)
@DisplayName ("Модульные тесты BooksController")
class BooksControllerTest {

    @Mock
    BookRestClient bookRestClient;

    @InjectMocks
    BooksController controller;

    @Test
    @DisplayName ("createBook создаст новую книгу и перенаправит на страницу книги в каталоге")
    void createBook_RequestIsValid_ReturnsRedirectionToBookPage ( ) {
        //given
        var payload = new NewBookPayload ("Новая книга", "Новый автор", 1111);
        var model = new ConcurrentModel ( );

        doReturn (new Book (1, "Новая книга", "Новый автор", 1111))
                .when (this.bookRestClient)
                .createBook ("Новая книга", "Новый автор", 1111);

        //when
        var result = this.controller.createBook (payload, model);

        //then
        assertEquals ("redirect:/catalogue/books/1", result);

        verify (this.bookRestClient).createBook ("Новая книга", "Новый автор", 1111);
        verifyNoMoreInteractions (this.bookRestClient);
    }

    @Test
    @DisplayName ("createBook покажет страницу с ошибками, если запрос был невалиден")
    void createBook_RequestIsInvalid_ReturnsBookFormWithErrors ( ) {
        //given
        var payload = new NewBookPayload ("", null, 111111);
        var model = new ConcurrentModel ( );

        doThrow (new BadRequestException (
                List.of ("Ошибка№1", "Ошибка№2", "Ошибка№3")))
                .when (this.bookRestClient)
                .createBook ("", null, 111111);

        //when
        var result = this.controller.createBook (payload, model);

        //then
        assertEquals ("catalogue/books/new_book", result);
        assertEquals (payload, model.getAttribute ("payload"));
        assertEquals (List.of ("Ошибка№1", "Ошибка№2", "Ошибка№3"),
                model.getAttribute ("errors"));

        verify (this.bookRestClient).createBook ("", null, 111111);
        verifyNoMoreInteractions (this.bookRestClient);
    }
}