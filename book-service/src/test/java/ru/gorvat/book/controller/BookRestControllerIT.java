package ru.gorvat.book.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@ExtendWith (RestDocumentationExtension.class)
public class BookRestControllerIT {

    @Autowired
    MockMvc mockMvc;

    @Test
    @Sql ("/sql/books.sql")
    void findBook_BookExists_ReturnsBooksList () throws Exception {

        var requestBuilder = MockMvcRequestBuilders.get ("/book-api/books/1")
                .with (jwt ().jwt (builder ->
                        builder.claim ("scope", "view_catalogue")));

        this.mockMvc.perform (requestBuilder)
                .andDo (print ())
                .andExpectAll (status().isOk(),
                        content().contentTypeCompatibleWith (MediaType.APPLICATION_JSON),
                        content ().json ("""
                        {"id": 1, "title": "Книга№1", "author": "Автор№1", "publication": 2021}
                        """)
        )
                .andDo (document("catalogue/books/find_all",
                        preprocessResponse(prettyPrint(), modifyHeaders()
                                .remove ("Vary")),
                        responseFields(
                                fieldWithPath ("id").description ("Идентификатор книги").type ("int"),
                                fieldWithPath ("title").description ("Название книги").type ("string"),
                                fieldWithPath ("author").description ("Автор книги").type ("string"),
                                fieldWithPath ("publication").description ("Публикация книги").type ("int")
                        )));
    }

    @Test
    void findBook_BookDoesNotExist_ReturnsNotFound() throws Exception {

        var requestBuilder = MockMvcRequestBuilders.get("/book-api/books/1")
                .with(jwt().jwt(builder ->
                        builder.claim("scope", "view_catalogue")));

        this.mockMvc.perform(requestBuilder)

                .andDo(print())
                .andExpectAll(
                        status().isNotFound()
                );
    }

    @Test
    @Sql("/sql/books.sql")
    void findBook_UserIsNotAuthorized_ReturnsForbidden() throws Exception {

        var requestBuilder = MockMvcRequestBuilders.get("/book-api/books/1")
                .with(jwt());


        this.mockMvc.perform(requestBuilder)

                .andDo(print())
                .andExpectAll(
                        status().isForbidden()
                );
    }

    @Test
    @Sql("/sql/books.sql")
    void updateBook_RequestIsValid_ReturnsNoContent() throws Exception {

        var requestBuilder = MockMvcRequestBuilders.patch("/book-api/books/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title": "Книга№1", "author": "Автор№1", "publication": 2021}
                        """)
                .with(jwt().jwt(builder ->
                        builder.claim("scope", "edit_catalogue")));


        this.mockMvc.perform(requestBuilder)

                .andDo(print())
                .andExpectAll(
                        status().isNoContent()
                );
    }

    @Test
    @Sql("/sql/books.sql")
    void updateBook_RequestIsInvalid_ReturnsBadRequest() throws Exception {

        var requestBuilder = MockMvcRequestBuilders.patch("/book-api/books/1")
                .locale(Locale.of("ru"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"title": "  ", "author": null, "publication": 2021}
                        """)
                .with(jwt().jwt(builder ->
                        builder.claim("scope", "edit_catalogue")));

        this.mockMvc.perform(requestBuilder)

                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON),
                        content().json("""
                                {
                                "errors" : [
                                "Поле title должно быть заполнено от 3 до 100 символов",
                                "Поле author не должно быть пустым"
                                ]
                                }""")
                );
    }

    @Test
    void updateBook_BookDoesNotExist_ReturnsNotFound() throws Exception {

        var requestBuilder = MockMvcRequestBuilders.patch("/book-api/books/1")
                .locale(Locale.of("ru"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                        "id" : 1,
                        "title" : "Новая книга",
                        "author" : "Новый автор",
                        "publication" : 2024
                        }""")
                .with(jwt().jwt(builder ->
                        builder.claim("scope", "edit_catalogue")));

        this.mockMvc.perform(requestBuilder)

                .andDo(print())
                .andExpectAll(
                        status().isNotFound()
                );
    }

    @Test
    void updateBook_UserIsNotAuthorized_ReturnsForbidden() throws Exception {

        var requestBuilder = MockMvcRequestBuilders.patch("/book-api/books/1")
                .locale(Locale.of("ru"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                        "id" : 1,
                        "title" : "Новая книга",
                        "author" : "Новый автор",
                        "publication" : 2024
                        }""")
                .with(jwt());

        this.mockMvc.perform(requestBuilder)

                .andDo(print())
                .andExpectAll(
                        status().isForbidden()
                );
    }

    @Test
    @Sql("/sql/books.sql")
    void deleteBook_BookExists_ReturnsNoContent() throws Exception {

        var requestBuilder = MockMvcRequestBuilders.delete("/book-api/books/1")
                .with(jwt().jwt(builder ->
                        builder.claim("scope", "edit_catalogue")));

        this.mockMvc.perform(requestBuilder)

                .andDo(print())
                .andExpectAll(
                        status().isNoContent()
                );
    }

    @Test
    void deleteBook_BookDoesNotExist_ReturnsNotFound() throws Exception {

        var requestBuilder = MockMvcRequestBuilders.delete("/book-api/books/1")
                .with(jwt().jwt(builder ->
                        builder.claim("scope", "edit_catalogue")));

        this.mockMvc.perform(requestBuilder)

                .andDo(print())
                .andExpectAll(
                        status().isNotFound()
                );
    }

    @Test
    void deleteBook_UserIsNotAuthorized_ReturnsForbidden() throws Exception {

        var requestBuilder = MockMvcRequestBuilders.delete("/book-api/books/1")
                .with(jwt());

        this.mockMvc.perform(requestBuilder)

                .andDo(print())
                .andExpectAll(
                        status().isForbidden()
                );
    }
}
