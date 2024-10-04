package ru.gorvat.book.controller;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.gorvat.book.controller.payload.NewBookPayload;
import ru.gorvat.book.entity.Book;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@WireMockTest (httpPort = 54321)
class BooksControllerIT {

    @Autowired
    MockMvc mockMvc;

    @Test
    void getBooksList_ReturnsBooksListPage () throws Exception {

        var requestBuilder = MockMvcRequestBuilders.get ("/catalogue/books/list").queryParam ("filter", "книга").with (user ("s.gorvat").roles ("MANAGER"));

        WireMock.stubFor (WireMock.get (WireMock.urlPathMatching ("/book-api/books")).withQueryParam ("filter", WireMock.equalTo ("книга")).willReturn (WireMock.ok ("""
                [
                {"id": 1, "title": "Книга№1", "author": "Автор№1", "publication": 2021},
                {"id": 2, "title": "Книга№2", "author": "Автор№2", "publication": 2022},
                {"id": 3, "title": "Книга№3", "author": "Автор№3", "publication": 2023}
                ]""").withHeader (HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));

        this.mockMvc.perform (requestBuilder)

                .andDo (print ())
                .andExpectAll (
                        status ().isOk (),
                        view ().name ("catalogue/books/list"),
                        model ().attribute ("filter", "книга"),
                        model ().attribute ("books", List.of (
                        new Book (1, "Книга№1", "Автор№1", 2021),
                        new Book (2, "Книга№2", "Автор№2", 2022),
                        new Book (3, "Книга№3", "Автор№3", 2023))));

        WireMock.verify (WireMock.getRequestedFor (WireMock.urlPathMatching ("/book-api/books")).withQueryParam ("filter", WireMock.equalTo ("книга")));
    }

    @Test
    void getBookList_UserIsNotAuthorized_ReturnsForbidden () throws Exception {

        var requestBuilder = MockMvcRequestBuilders.get ("/catalogue/books/list").queryParam ("filter", "книга").with (user ("e.gorvat"));

        this.mockMvc.perform (requestBuilder)

                .andDo (print ()).andExpectAll (status ().isForbidden ());
    }

    @Test
    void getNewBook_ReturnsBook () throws Exception {
        var requestBuilder = MockMvcRequestBuilders.get ("/catalogue/books/create").with (user ("s.gorvat").roles ("MANAGER"));

        this.mockMvc.perform (requestBuilder)

                .andDo (print ()).andExpectAll (status ().isOk (), view ().name ("catalogue/books/new_book"));
    }

    @Test
    void getNewBook_UserIsNotAuthorized_ReturnsForbidden () throws Exception {
        var requestBuilder = MockMvcRequestBuilders.get ("/catalogue/books/create").with (user ("e.gorvat"));

        this.mockMvc.perform (requestBuilder)

                .andDo (print ()).andExpectAll (status ().isForbidden ());
    }

    @Test
    void createBook_RequestIsValid_RedirectsToPageBook () throws Exception {
        var requestBuilder = MockMvcRequestBuilders.post ("/catalogue/books/create")
                .param ("title", "Новая книга")
                .param ("author", "Новый автор")
                .param ("publication", "2024")
                .with (user ("s.gorvat").roles ("MANAGER"))
                .with (csrf ());

        WireMock.stubFor (WireMock.post (WireMock.urlPathMatching ("/book-api/books"))
                .withRequestBody (WireMock.equalToJson ("""
                {
                "title": "Новая книга",
                "author": "Новый автор",
                "publication": 2024
                }
                """))
                .willReturn (WireMock.created ()
                        .withHeader (HttpHeaders.CONTENT_TYPE,
                MediaType.APPLICATION_JSON_VALUE).withBody ("""
                {
                "id": 1,
                "title": "Новая книга",
                "author": "Новый автор",
                "publication": 2024
                }
                """)));

        this.mockMvc.perform (requestBuilder)
                .andDo (print ())
                .andExpectAll (status ().is3xxRedirection (),
                        header ().string (HttpHeaders.LOCATION, "/catalogue/books/1"));

        WireMock.verify (WireMock.postRequestedFor (
                WireMock.urlPathMatching ("/book-api/books"))
                .withRequestBody (WireMock.equalToJson ("""
                {
                "title": "Новая книга",
                "author": "Новый автор",
                "publication": 2024
                }
                """)));
    }

    @Test
    void createBook_RequestIsInvalid_ReturnsNewPageBook () throws Exception {
        var requestBuilder =
                MockMvcRequestBuilders.post("/catalogue/books/create")
                .param("title", "   ")
                .param("publication", "0")
                .with(user("s.gorvat").roles("MANAGER"))
                .with(csrf());

        WireMock.stubFor(WireMock.post(WireMock.urlPathMatching("/book-api/books"))
                .withRequestBody(WireMock.equalToJson("""
                        {
                            "title": "   ",
                            "author": null,
                            "publication": 0
                        }"""))
                .willReturn(WireMock.badRequest()
                        .withHeader(HttpHeaders.CONTENT_TYPE,
                                MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                        .withBody("""
                                {
                                    "errors": ["Ошибка 1", "Ошибка 2", "Ошибка 3"]
                                }""")));

        this.mockMvc.perform(requestBuilder)

                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        view().name("catalogue/books/new_book"),
                        model().attribute("payload", new NewBookPayload ("   ", null, 0)),
                        model().attribute("errors", List.of("Ошибка 1", "Ошибка 2", "Ошибка 3"))
                );

        WireMock.verify(WireMock.postRequestedFor(
                WireMock.urlPathMatching("/book-api/books"))
                .withRequestBody(WireMock.equalToJson("""
                        {
                            "title": "   ",
                            "author": null,
                            "publication": 0
                        }""")));
    }


    @Test
    void createBook_UserIsNotAuthorized_ReturnsForbidden () throws Exception {
        var requestBuilder = MockMvcRequestBuilders.post ("/catalogue/books/create")
                .param ("title", "Новая книга")
                .param ("author", "Новый автор")
                .param ("publication", "Новая дата публикации")
                .with (user ("e.gorvat"))
                .with (csrf ());

        this.mockMvc.perform (requestBuilder)

                .andDo (print ()).andExpectAll (status ().isForbidden ());
    }
}
