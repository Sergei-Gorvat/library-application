package ru.gorvat.book.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class BooksRestControllerIT {

    @Autowired
    MockMvc mockMvc;

    @Test
    @Sql ("/sql/books.sql")
    void findBooks_ReturnsBooksList ( ) throws Exception {

        var requestBuilder = MockMvcRequestBuilders.get ("/book-api/books")
                .param ("filter", "книга")
                .with (jwt ( ).jwt (builder ->
                        builder.claim ("scope", "view_catalogue")));

        this.mockMvc.perform (requestBuilder)

                .andDo (print ( ))
                .andExpectAll (
                        status ( ).isOk ( ),
                        content ( ).contentTypeCompatibleWith (MediaType.APPLICATION_JSON),
                        content ( ).json ("""
                                [
                                {"id": 1, "title": "Книга№1", "author": "Автор№1", "publication": 2021},
                                {"id": 2, "title": "Книга№2", "author": "Автор№2", "publication": 2022},
                                {"id": 3, "title": "Книга№3", "author": "Автор№3", "publication": 2023}
                                ]
                                """)
                );
    }

    @Test
    void createBook_RequestIsValid_ReturnsNewBook ( ) throws Exception {
        var requestBuilder = MockMvcRequestBuilders.post ("/book-api/books")
                .contentType (MediaType.APPLICATION_JSON)
                .content ("""
                        {"title" : "Новая книга", "author" : "Новый автор", "publication" : 2024}
                        """)
                .with (jwt ( ).jwt (builder ->
                        builder.claim ("scope", "edit_catalogue")));

        this.mockMvc.perform (requestBuilder)

                .andDo (print ( ))
                .andExpectAll (
                        status ( ).isCreated ( ),
                        header ( ).string (HttpHeaders.LOCATION, "http://localhost/book-api/books/1"),
                        content ( ).contentTypeCompatibleWith (MediaType.APPLICATION_JSON),
                        content ( ).json ("""
                                {
                                "id" : 1,
                                 "title" : "Новая книга",
                                  "author" : "Новый автор",
                                  "publication" : 2024
                                    }
                                """));
    }

    @Test
    void createBook_RequestIsInvalid_ReturnsProblemDetail ( ) throws Exception {
        var requestBuilder = MockMvcRequestBuilders.post ("/book-api/books")
                .contentType (MediaType.APPLICATION_JSON)
                .content ("""
                        {"title" : " ", "author" : null, "publication" : 1}
                        """)
                .locale (Locale.of ("ru", "RU"))
                .with (jwt ( ).jwt (builder ->
                        builder.claim ("scope", "edit_catalogue")));

        this.mockMvc.perform (requestBuilder)

                .andDo (print ( ))
                .andExpectAll (
                        status ( ).isBadRequest ( ),
                        content ( ).contentTypeCompatibleWith (MediaType.APPLICATION_PROBLEM_JSON),
                        content ( ).json ("""
                                {
                                "errors" : [
                                "Поле title должно быть заполнено от 3 до 100 символов",
                                "Поле author не должно быть пустым"
                                ]
                                    }"""));
    }

    @Test
    void createBook_UserIsNotAuthorized_ReturnsForbidden ( ) throws Exception {
        var requestBuilder = MockMvcRequestBuilders.post ("/book-api/books")
                .contentType (MediaType.APPLICATION_JSON)
                .content ("""
                        {"title" : " ", "author" : null, "publication" : 1}
                        """)
                .locale (Locale.of ("ru", "RU"))
                .with (jwt ( ).jwt (builder ->
                        builder.claim ("scope", "view_catalogue")));

        this.mockMvc.perform (requestBuilder)

                .andDo (print ( ))
                .andExpectAll (
                        status ( ).isForbidden ( ));
    }
}