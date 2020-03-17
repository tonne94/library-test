package com.example.test.application;

import com.example.test.domain.model.Book;
import com.example.test.domain.model.BookRecord;

import java.util.List;
import java.util.Optional;

public interface BookRecordService {

    Optional<Book> addBookRecord(Book book);

    Optional<Book> invalidateBookRecord(Book book, Long bookRecordId);
}
