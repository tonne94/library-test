package com.example.test.application;

import com.example.test.domain.model.Book;

import java.util.Optional;

public interface BookService {
    Optional<Book> createNewBook(Book book);
}
