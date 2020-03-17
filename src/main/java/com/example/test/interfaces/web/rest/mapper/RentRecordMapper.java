package com.example.test.interfaces.web.rest.mapper;

import com.example.test.domain.model.RentRecord;
import com.example.test.dto.RentRecordDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", uses = {BookRecordMapper.class, AccountMapper.class})
public interface RentRecordMapper {

    RentRecord rentRecordDTOToRentRecord(RentRecordDTO rentRecordDTO);

    RentRecordDTO rentRecordToRentRecordDTO(RentRecord rentRecord);

    List<RentRecord> rentRecordDTOsToRentRecords(List<RentRecordDTO> rentRecordDTOs);

    List<RentRecordDTO> rentRecordsToRentRecordDTOs(List<RentRecord> rentRecords);

    @FromRented
    @Mapping(target = "bookRecord", qualifiedBy = FromAccount.class)
    @Mapping(target = "account", qualifiedBy = FromRented.class)
    RentRecordDTO rentRecordToRentRecordDTOFromRented(RentRecord rentRecord);

    @FromRented
    default List<RentRecordDTO> rentRecordsToRentRecordDTOsFromRented(List<RentRecord> rentRecords) {
        if (rentRecords == null) {
            return null;
        }
        List<RentRecordDTO> resultList = new ArrayList<>();
        for (RentRecord rentRecord : rentRecords) {
            resultList.add(rentRecordToRentRecordDTOFromRented(rentRecord));
        }
        return resultList;
    }

    @FromAccount
    @Mapping(target = "bookRecord", qualifiedBy = FromAccount.class)
    @Mapping(target = "account", ignore = true)
    RentRecordDTO rentRecordToRentRecordDTOFromAccount(RentRecord rentRecord);

    @FromAccount
    default List<RentRecordDTO> rentRecordsToRentRecordDTOsFromAccount(List<RentRecord> rentRecords) {
        if (rentRecords == null) {
            return null;
        }
        List<RentRecordDTO> resultList = new ArrayList<>();
        for (RentRecord rentRecord : rentRecords) {
            resultList.add(rentRecordToRentRecordDTOFromAccount(rentRecord));
        }
        return resultList;
    }

}
