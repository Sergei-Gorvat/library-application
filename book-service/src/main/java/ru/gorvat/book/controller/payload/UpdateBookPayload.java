package ru.gorvat.book.controller.payload;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateBookPayload(

        @NotNull (message = "{books.errors.update.title_is_null}")
        @Size (min = 3, max = 100, message = "{books.errors.update.title_size_is_invalid}")
        String title,

        @NotNull (message = "{books.errors.update.author_is_null}")
        @Size (min = 3, max = 100, message = "{books.errors.update.author_size_is_invalid}")
        String author,

        @NotNull (message = "{books.errors.update.publication_is_null}")
        @Min (value = 1, message = "{books.errors.update.publication_min}")
        Integer publication) {
}
