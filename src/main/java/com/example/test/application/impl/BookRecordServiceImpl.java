package com.example.test.application.impl;

import com.example.test.application.BookRecordService;
import com.example.test.domain.model.Book;
import com.example.test.domain.model.BookRecord;
import com.example.test.domain.repository.BookRecordRepository;
import com.example.test.domain.repository.BookRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
public class BookRecordServiceImpl implements BookRecordService {

    private BookRepository bookRepository;
    private BookRecordRepository bookRecordRepository;

    public BookRecordServiceImpl(BookRepository bookRepository, BookRecordRepository bookRecordRepository) {
        this.bookRepository = bookRepository;
        this.bookRecordRepository = bookRecordRepository;
    }

    /**
     * adding a book record for a book
     * @param book
     * @return Optional of book
     */
    @Transactional
    @Override
    public Optional<Book> addBookRecord(Book book) {
        book.getBookRecords().add(new BookRecord(book));
        return Optional.of(bookRepository.save(book));
    }

    /**
     * invalidating a book record
     * @param book
     * @param bookRecordId
     * @return Optional of book
     */
    @Transactional
    @Override
    public Optional<Book> invalidateBookRecord(Book book, Long bookRecordId) {
        BookRecord bookRecord = book.getBookRecords().stream().filter(bookRecord1 -> bookRecord1.getId().equals(bookRecordId)).findFirst().get();
        bookRecord.setInvalid(true);
        bookRecordRepository.save(bookRecord);
        Book result = bookRepository.getOne(book.getId());
        return Optional.of(result);
    }
}
