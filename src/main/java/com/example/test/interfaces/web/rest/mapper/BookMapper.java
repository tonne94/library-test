package com.example.test.interfaces.web.rest.mapper;

import com.example.test.domain.model.Book;
import com.example.test.dto.BookDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", uses = {BookRecordMapper.class, AuthorMapper.class})
public interface BookMapper {

    Book bookDTOToBook(BookDTO bookDTO);

    @Mapping(target = "bookRecords", ignore = true)
    @Mapping(target = "authors", ignore = true)
    BookDTO bookToBookDTO(Book book);

    List<BookDTO> booksToBookDTOs(List<Book> books);

    List<Book> bookDTOsToBooks(List<BookDTO> bookDTOs);

    @FromBook
    @Mapping(target = "bookRecords", qualifiedBy = FromBook.class)
    @Mapping(target = "authors", qualifiedBy = FromBook.class)
    BookDTO bookToBookDTOFromBook(Book book);

    @FromBook
    default List<BookDTO> booksToBookDTOsFromBook(List<Book> books) {
        if (books == null) {
            return null;
        }
        List<BookDTO> resultList = new ArrayList<>();
        for (Book book : books) {
            resultList.add(bookToBookDTOFromBook(book));
        }
        return resultList;
    }

    @FromAccount
    @Mapping(target = "bookRecords", ignore = true)
    @Mapping(target = "authors", qualifiedBy = FromAccount.class)
    BookDTO bookToBookDTOFromAccount(Book book);

    @FromAuthor
    @Mapping(target = "bookRecords", ignore = true)
    @Mapping(target = "authors", ignore = true)
    BookDTO bookToBookDTOFromAuthor(Book book);

    @FromAuthor
    default List<BookDTO> booksToBookDTOsFromAuthor(List<Book> books) {
        if (books == null) {
            return null;
        }
        List<BookDTO> resultList = new ArrayList<>();
        for (Book book : books) {
            resultList.add(bookToBookDTOFromAuthor(book));
        }
        return resultList;
    }
}
