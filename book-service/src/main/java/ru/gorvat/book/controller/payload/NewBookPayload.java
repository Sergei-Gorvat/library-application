package ru.gorvat.book.controller.payload;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record NewBookPayload(

        @NotNull (message = "{books.errors.create.title_is_null}")
        @Size (min = 3, max = 100, message = "{books.errors.create.title_size_is_invalid}")
        String title,

        @NotNull (message = "{books.errors.create.author_is_null}")
        @Size (min = 3, max = 100, message = "{books.errors.create.author_size_is_invalid}")
        String author,

        @NotNull (message = "{books.errors.create.publication_is_null}")
        @Min (value = 1, message = "{books.errors.create.publication_min}")
        Integer publication) {
}
