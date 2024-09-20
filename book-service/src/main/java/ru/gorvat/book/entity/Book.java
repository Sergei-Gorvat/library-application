package ru.gorvat.book.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table (schema = "catalogue", name = "t_book")
@NamedQueries (
        @NamedQuery (
                name = "Book.findAllByTitleLikeIgnoredCase",
                query = "select b from Book b where b.title ilike :filter"
        )
)
public class Book {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column (name = "c_title")
    @NotNull
    @Size (min = 3, max = 100)
    private String title;

    @Column (name = "c_author")
    @NotNull
    @Size (min = 3, max = 100)
    private String author;

    @Column (name = "c_publication")
    @NotNull
    private int publication;
}
