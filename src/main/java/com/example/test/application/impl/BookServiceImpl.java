package com.example.test.application.impl;

import com.example.test.application.BookService;
import com.example.test.domain.model.Author;
import com.example.test.domain.model.Book;
import com.example.test.domain.repository.AuthorRepository;
import com.example.test.domain.repository.BookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BookServiceImpl implements BookService {
    Logger log = LoggerFactory.getLogger(BookServiceImpl.class);

    private AuthorRepository authorRepository;
    private BookRepository bookRepository;

    public BookServiceImpl(AuthorRepository authorRepository, BookRepository bookRepository) {
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
    }

    @Transactional
    @Override
    public Optional<Book> createNewBook(Book book) {
        log.info("Creating new book");
        Set<Author> authorSet = book.getAuthors();
        Set<Author> authorSetToSave = authorSet.stream().filter(author -> author.getId()==null).collect(Collectors.toSet());
        authorRepository.saveAll(authorSetToSave);
        bookRepository.save(book);
        return Optional.of(book);
    }
}
