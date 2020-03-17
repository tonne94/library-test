package com.example.test.interfaces.web.rest;

import com.example.test.application.BookRecordService;
import com.example.test.application.BookService;
import com.example.test.domain.model.Book;
import com.example.test.domain.model.BookRecord;
import com.example.test.domain.model.RentRecord;
import com.example.test.domain.repository.BookRepository;
import com.example.test.domain.repository.RentRecordRepository;
import com.example.test.dto.ApiErrorDTO;
import com.example.test.dto.BookDTO;
import com.example.test.interfaces.web.rest.JsonView.BookView;
import com.example.test.interfaces.web.rest.mapper.BookMapper;
import com.fasterxml.jackson.annotation.JsonView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/books")
public class BookController {
    Logger log = LoggerFactory.getLogger(BookController.class);

    private BookRepository bookRepository;
    private RentRecordRepository rentRecordRepository;

    private BookMapper bookMapper;

    private BookService bookService;
    private BookRecordService bookRecordService;

    public BookController(BookRepository bookRepository,
                          BookMapper bookMapper,
                          BookRecordService bookRecordService,
                          BookService bookService,
                          RentRecordRepository rentRecordRepository) {
        this.bookRepository = bookRepository;
        this.bookService = bookService;
        this.bookMapper = bookMapper;
        this.bookRecordService = bookRecordService;
        this.rentRecordRepository = rentRecordRepository;
    }

    @GetMapping
    @JsonView(BookView.class)
    public ResponseEntity<List<BookDTO>> getBooks() {
        log.info("Getting all books");
        return new ResponseEntity<>(bookMapper.booksToBookDTOsFromBook(bookRepository.findAll()), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @JsonView(BookView.class)
    public ResponseEntity<BookDTO> getBook(@PathVariable Long id) {
        log.info("Getting a book with id: " + id);
        Optional<Book> optionalBook = bookRepository.findById(id);
        return optionalBook.map(book -> new ResponseEntity<>(bookMapper.bookToBookDTOFromBook(book), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));

    }

    @PostMapping
    @JsonView(BookView.class)
    public ResponseEntity<BookDTO> createBook(@Valid @RequestBody BookDTO bookDTO) {
        log.info("Creating a book");
        Optional<Book> result = bookService.createNewBook(bookMapper.bookDTOToBook(bookDTO));
        return result.map(book -> new ResponseEntity<>(bookMapper.bookToBookDTOFromBook(book), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }

    @PostMapping("/{id}/add-book-record")
    @JsonView(BookView.class)
    public ResponseEntity<?> addBookRecord(@PathVariable Long id) {
        log.info("Add book record for book id: " + id);
        Optional<Book> bookOptional = bookRepository.findById(id);
        if (bookOptional.isEmpty()) {
            log.error("Book is not found for book id: " + id);
            return new ResponseEntity<>(new ApiErrorDTO(HttpStatus.NOT_FOUND.value(), "Book is not found for book id: " + id), HttpStatus.NOT_FOUND);
        }
        Optional<Book> result = bookRecordService.addBookRecord(bookOptional.get());
        return result.map(book -> new ResponseEntity<>(bookMapper.bookToBookDTOFromBook(book), HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }

    @DeleteMapping("/{bookId}/invalidate-book-record/{bookRecordId}")
    @JsonView(BookView.class)
    public ResponseEntity<?> invalidateBookRecord(@PathVariable Long bookId, @PathVariable Long bookRecordId) {
        log.info("Invalidate book record for book id: " + bookId + " and book record id: " + bookRecordId);
        Optional<Book> bookOptional = bookRepository.findById(bookId);
        if (bookOptional.isEmpty()) {
            log.error("Book is not found by id: " + bookId);
            return new ResponseEntity<>(new ApiErrorDTO(HttpStatus.BAD_REQUEST.value(), "Book is not found"), HttpStatus.BAD_REQUEST);
        } else {
            Optional<BookRecord> bookRecordOptional = bookOptional.get().getBookRecords().stream().filter(bookRecord -> bookRecord.getId().equals(bookRecordId)).findFirst();
            if (bookRecordOptional.isEmpty()) {
                log.error("Book record id: " + bookRecordId +
                        " does not belong to book id: " + bookId);
                return new ResponseEntity<>(new ApiErrorDTO(HttpStatus.BAD_REQUEST.value(), "Book record id: " + bookRecordId +
                        " does not belong to book id: " + bookId), HttpStatus.BAD_REQUEST);
            }
        }
        Optional<RentRecord> rentRecordOptional = rentRecordRepository.findByBookRecordIdAndActualReturnTimeIsNull(bookRecordId);
        if (rentRecordOptional.isPresent()) {
            log.error("Cant remove book record if the book is rented. Book record id: " + bookRecordId + " Book id: " + bookId);
            return new ResponseEntity<>(new ApiErrorDTO(HttpStatus.BAD_REQUEST.value(), "Cant remove book record if the book is rented. Book record id: " + bookRecordId + " Book id: " + bookId), HttpStatus.BAD_REQUEST);
        }

        Optional<Book> result = bookRecordService.invalidateBookRecord(bookOptional.get(), bookRecordId);
        return result.map(book -> new ResponseEntity<>(bookMapper.bookToBookDTOFromBook(book), HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
