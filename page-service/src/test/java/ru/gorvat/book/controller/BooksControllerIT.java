package ru.gorvat.book.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;


@SpringBootTest
@AutoConfigureMockMvc
class BooksControllerIT {

    @Autowired
    MockMvc mockMvc;

    @Test
    void getNewBook_ReturnsBook ( ) throws Exception {
        var requestBuilder = MockMvcRequestBuilders.get ("/catalogue/books/create")
                .with (user ("s.gorvat").roles ("MANAGER"));

        this.mockMvc.perform (requestBuilder)

                .andDo (print ( ))
                .andExpectAll (status ( ).isOk ( ),
                        view ( ).name ("catalogue/books/new_book")
                );
    }
}
