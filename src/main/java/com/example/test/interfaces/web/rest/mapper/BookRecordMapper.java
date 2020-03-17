package com.example.test.interfaces.web.rest.mapper;

import com.example.test.domain.model.BookRecord;
import com.example.test.dto.BookRecordDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", uses = {BookMapper.class, RentRecordMapper.class})
public interface BookRecordMapper {

    BookRecord bookRecordDTOtoBookRecord(BookRecordDTO bookRecordDTO);

    @Mapping(target = "book", ignore = true)
    @Mapping(target = "rentRecords", ignore = true)
    BookRecordDTO bookRecordToBookRecordDTO(BookRecord bookRecord);

    List<BookRecordDTO> bookRecordsToBookRecordDTOs(List<BookRecord> bookRecords);

    List<BookRecord> bookRecordDTOsToBookRecords(List<BookRecordDTO> bookRecordDTOs);

    @FromBook
    @Mapping(target = "book", ignore = true)
    @Mapping(target = "rentRecords", ignore = true)
    BookRecordDTO bookRecordToBookRecordDTOFromBook(BookRecord bookRecord);

    @FromBook
    default List<BookRecordDTO> bookRecordsToBookRecordDTOsFromBook(List<BookRecord> bookRecords) {
        if (bookRecords == null) {
            return null;
        }
        List<BookRecordDTO> resultList = new ArrayList<>();
        for (BookRecord bookRecord : bookRecords) {
            resultList.add(bookRecordToBookRecordDTOFromBook(bookRecord));
        }
        return resultList;
    }

    @FromAccount
    @Mapping(target = "rentRecords", ignore = true)
    BookRecordDTO bookRecordToBookRecordDTOFromAccount(BookRecord bookRecord);

    @FromAccount
    default List<BookRecordDTO> bookRecordsToBookRecordDTOsFromAccount(List<BookRecord> bookRecords) {
        if (bookRecords == null) {
            return null;
        }
        List<BookRecordDTO> resultList = new ArrayList<>();
        for (BookRecord bookRecord : bookRecords) {
            resultList.add(bookRecordToBookRecordDTOFromAccount(bookRecord));
        }
        return resultList;
    }

    @FromRented
    @Mapping(target = "book", ignore = true)
    @Mapping(target = "rentRecords", qualifiedBy = FromRented.class)
    BookRecordDTO bookRecordToBookRecordDTOFromRented(BookRecord bookRecord);

    @FromRented
    default List<BookRecordDTO> bookRecordsToBookRecordDTOsFromRented(List<BookRecord> bookRecords) {
        if (bookRecords == null) {
            return null;
        }
        List<BookRecordDTO> resultList = new ArrayList<>();
        for (BookRecord bookRecord : bookRecords) {
            resultList.add(bookRecordToBookRecordDTOFromRented(bookRecord));
        }
        return resultList;
    }

}
