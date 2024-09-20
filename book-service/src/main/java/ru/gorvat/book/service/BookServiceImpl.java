package ru.gorvat.book.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.gorvat.book.entity.Book;
import ru.gorvat.book.repository.BookRepository;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    @Override
    public Iterable <Book> findAllBooks (String filter) {
        if (filter != null && !filter.isBlank ()) {
            return this.bookRepository.findAllByTitleLikeIgnoreCase ("%" + filter + "%");
        } else {
            return this.bookRepository.findAll ();
        }
    }

    @Override
    @Transactional
    public Book createBook (String title, String author, int publication) {
        return this.bookRepository.save (new Book (null, title, author, publication));
    }

    @Override
    public Optional <Book> findBook (int bookId) {
        return this.bookRepository.findById (bookId);
    }

    @Override
    @Transactional
    public void updateBook (Integer id, String title, String author, int publication) {
        this.bookRepository.findById (id).ifPresentOrElse (book -> {
            book.setTitle (title);
            book.setAuthor (author);
            book.setPublication (publication);

//                    this.bookRepository.save (book);  один из способов сохранения изменений в бд
        }, () -> {
            throw new NoSuchElementException ();
        });
    }

    @Override
    @Transactional
    public void deleteBook (Integer id) {
        this.bookRepository.deleteById (id);
    }
}
