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
import ru.gorvat.book.controller.payload.UpdateBookPayload;
import ru.gorvat.book.entity.Book;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@WireMockTest(httpPort = 54321)
public class BookControllerIT {

    @Autowired
    MockMvc mockMvc;

    @Test
    void getBook_BookExists_ReturnsBookPage() throws Exception {

        var requestBuilder =
                MockMvcRequestBuilders.get("/catalogue/books/1")
                .with(user("s.gorvat").roles("MANAGER"));

        WireMock.stubFor(WireMock.get("/book-api/books/1")
                .willReturn(WireMock.okJson("""
                        {
                            "id": 1,
                            "title": "Книга",
                            "author": "Автор",
                            "publication": 2024
                        }
                        """)));

        this.mockMvc.perform(requestBuilder)

                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        view().name("catalogue/books/book"),
                        model().attribute("book",
                                new Book (1, "Книга", "Автор", 2024))
                );
    }

    @Test
    void getBook_BookDoesNotExist_ReturnsError404Page() throws Exception {

        var requestBuilder = MockMvcRequestBuilders.get("/catalogue/books/1")
                .with(user("s.gorvat").roles("MANAGER"));

        WireMock.stubFor(WireMock.get("/book-api/books/1")
                .willReturn(WireMock.notFound()));

        this.mockMvc.perform(requestBuilder)

                .andDo(print())
                .andExpectAll(
                        status().isNotFound(),
                        view().name("errors/404"),
                        model().attribute("error", "Такой книги нет в каталоге")
                );
    }

    @Test
    void getBook_UserIsNotAuthorized_ReturnsForbidden() throws Exception {

        var requestBuilder =
                MockMvcRequestBuilders.get("/catalogue/books/1")
                .with(user("e.gorvat"));

        this.mockMvc.perform(requestBuilder)

                .andDo(print())
                .andExpectAll(
                        status().isForbidden()
                );
    }

    @Test
    void getBookEditPage_BookExists_ReturnsBookEditPage() throws Exception {

        var requestBuilder =
                MockMvcRequestBuilders.get("/catalogue/books/1/edit")
                .with(user("s.gorvat").roles("MANAGER"));

        WireMock.stubFor(WireMock.get("/book-api/books/1")
                .willReturn(WireMock.okJson("""
                        {
                            "id": 1,
                            "title": "Книга",
                            "author": "Автор",
                            "publication": 2024
                        }
                        """)));

        this.mockMvc.perform(requestBuilder)

                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        view().name("catalogue/books/edit"),
                        model().attribute("book",
                                new Book (1, "Книга", "Автор", 2024))
                );
    }

    @Test
    void getBookEditPage_BookDoesNotExist_ReturnsError404Page() throws Exception {

        var requestBuilder =
                MockMvcRequestBuilders.get("/catalogue/books/1/edit")
                .with(user("s.gorvat").roles("MANAGER"));

        WireMock.stubFor(WireMock.get("/book-api/books/1")
                .willReturn(WireMock.notFound()));

        this.mockMvc.perform(requestBuilder)

                .andDo(print())
                .andExpectAll(
                        status().isNotFound(),
                        view().name("errors/404"),
                        model().attribute("error", "Такой книги нет в каталоге")
                );
    }

    @Test
    void getBookEditPage_UserIsNotAuthorized_ReturnsForbidden() throws Exception {

        var requestBuilder =
                MockMvcRequestBuilders.get("/catalogue/books/1/edit")
                .with(user("e.gorvat"));

        this.mockMvc.perform(requestBuilder)

                .andDo(print())
                .andExpectAll(
                        status().isForbidden()
                );
    }

    @Test
    void updateBook_RequestIsValid_RedirectsToBookPage() throws Exception {

        var requestBuilder = MockMvcRequestBuilders.post("/catalogue/products/1/edit")
                .param("title", "Новое название")
                .param("author", "Новый автор")
                .param("publication", "Новая дата публикации")
                .with(user("s.gorvat").roles("MANAGER"))
                .with(csrf());

        WireMock.stubFor(WireMock.get("/book-api/books/1")
                .willReturn(WireMock.okJson("""
                        {
                            "id": 1,
                            "title": "Книга",
                            "author": "Автор",
                            "publication": 2024
                        }
                        """)));

        WireMock.stubFor(WireMock.patch("/book-api/books/1")
                .withRequestBody(WireMock.equalToJson("""
                        {
                            "title": "Новое название",
                            "author": "Новый автор",
                            "publication": "Новая дата публикации"
                        }"""))
                .willReturn(WireMock.noContent()));

        this.mockMvc.perform(requestBuilder)

                .andDo(print())
                .andExpectAll(
                        status().is3xxRedirection(),
                        redirectedUrl("/catalogue/books/1")
                );

        WireMock.verify(WireMock.patchRequestedFor(WireMock.urlPathMatching("/book-api/books/1"))
                .withRequestBody(WireMock.equalToJson("""
                        {
                            "title": "Новое название",
                            "author": "Новый автор",
                            "publication": "Новая дата публикации"
                        }""")));
    }

    @Test
    void updateBook_RequestIsInvalid_ReturnsBookEditPage() throws Exception {

        var requestBuilder = MockMvcRequestBuilders.post("/catalogue/books/1/edit")
                .param("title", "   ")
                .with(user("s.gorvat").roles("MANAGER"))
                .with(csrf());

        WireMock.stubFor(WireMock.get("/book-api/books/1")
                .willReturn(WireMock.okJson("""
                        {
                            "id": 1,
                            "title": "Книга",
                            "author": "Автор",
                            "publication": 2024
                        }
                        """)));

        WireMock.stubFor(WireMock.patch("/book-api/books/1")
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
                        view().name("catalogue/books/edit"),
                        model().attribute("book", new Book (1, "Книга", "Автор", 2024)),
                        model().attribute("errors", List.of("Ошибка 1", "Ошибка 2", "Ошибка 3")),
                        model().attribute("payload", new UpdateBookPayload ("   ", null, 0))
                );

        WireMock.verify(WireMock.patchRequestedFor(WireMock.urlPathMatching("/book-api/books/1"))
                .withRequestBody(WireMock.equalToJson("""
                        {
                            "title": "   ",
                            "author": null,
                            "publication": 0
                        }""")));
    }

    @Test
    void updateProduct_ProductDoesNotExist_ReturnsError404Page() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.post("/catalogue/products/1/edit")
                .param("title", "Новое название")
                .param("details", "Новое описание товара")
                .with(user("j.dewar").roles("MANAGER"))
                .with(csrf());

        WireMock.stubFor(WireMock.get("/catalogue-api/products/1")
                .willReturn(WireMock.notFound()));

        // when
        this.mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isNotFound(),
                        view().name("errors/404"),
                        model().attribute("error", "Товар не найден")
                );
    }

    @Test
    void updateProduct_UserIsNotAuthorized_ReturnsForbidden() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.post("/catalogue/products/1/edit")
                .param("title", "Новое название")
                .param("details", "Новое описание товара")
                .with(user("j.daniels"))
                .with(csrf());

        // when
        this.mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isForbidden()
                );
    }

    @Test
    void deleteProduct_ProductExists_RedirectsToProductsListPage() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.post("/catalogue/products/1/delete")
                .with(user("j.dewar").roles("MANAGER"))
                .with(csrf());

        WireMock.stubFor(WireMock.get("/catalogue-api/products/1")
                .willReturn(WireMock.okJson("""
                        {
                            "id": 1,
                            "title": "Товар",
                            "details": "Описание товара"
                        }
                        """)));

        WireMock.stubFor(WireMock.delete("/catalogue-api/products/1")
                .willReturn(WireMock.noContent()));

        // when
        this.mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().is3xxRedirection(),
                        redirectedUrl("/catalogue/products/list")
                );

        WireMock.verify(WireMock.deleteRequestedFor(WireMock.urlPathMatching("/catalogue-api/products/1")));
    }

    @Test
    void deleteProduct_ProductDoesNotExist_ReturnsError404Page() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.post("/catalogue/products/1/delete")
                .with(user("j.dewar").roles("MANAGER"))
                .with(csrf());

        WireMock.stubFor(WireMock.get("/catalogue-api/products/1")
                .willReturn(WireMock.notFound()));

        // when
        this.mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isNotFound(),
                        view().name("errors/404"),
                        model().attribute("error", "Товар не найден")
                );
    }

    @Test
    void deleteProduct_UserIsNotAuthorized_ReturnsForbidden() throws Exception {
        // given
        var requestBuilder = MockMvcRequestBuilders.post("/catalogue/products/1/delete")
                .with(user("j.daniels"))
                .with(csrf());

        // when
        this.mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isForbidden()
                );
    }
}
