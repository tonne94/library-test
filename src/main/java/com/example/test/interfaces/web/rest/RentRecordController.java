package com.example.test.interfaces.web.rest;

import com.example.test.domain.model.RentRecord;
import com.example.test.domain.repository.RentRecordRepository;
import com.example.test.dto.ApiErrorDTO;
import com.example.test.dto.RentRecordDTO;
import com.example.test.interfaces.web.rest.JsonView.RentedBookView;
import com.example.test.interfaces.web.rest.mapper.RentRecordMapper;
import com.fasterxml.jackson.annotation.JsonView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/rent-records")
public class RentRecordController {
    Logger log = LoggerFactory.getLogger(RentRecordController.class);

    private RentRecordRepository rentRecordRepository;
    private RentRecordMapper rentRecordMapper;

    public RentRecordController(RentRecordMapper rentRecordMapper,
                                RentRecordRepository rentRecordRepository) {
        this.rentRecordMapper = rentRecordMapper;
        this.rentRecordRepository = rentRecordRepository;
    }

    @GetMapping
    @JsonView(RentedBookView.class)
    public ResponseEntity<List<RentRecordDTO>> getRentRecords(
            @RequestParam(value = "only-rented", required = false) boolean onlyRented) {
        log.info("Getting all rent records");
        if (onlyRented) {
            return new ResponseEntity<>(rentRecordMapper.rentRecordsToRentRecordDTOsFromRented(rentRecordRepository.findAllOnlyRented()), HttpStatus.OK);
        }
        return new ResponseEntity<>(rentRecordMapper.rentRecordsToRentRecordDTOsFromRented(rentRecordRepository.findAll()), HttpStatus.OK);
    }

    @GetMapping("/account/{id}")
    @JsonView(RentedBookView.class)
    public ResponseEntity<List<RentRecordDTO>> getRentRecordsByAccountId(@PathVariable Long id,
                                                                         @RequestParam(value = "only-rented", required = false) boolean onlyRented) {
        log.info("Getting all rent records by account id");
        if (onlyRented) {
            return new ResponseEntity<>(rentRecordMapper.rentRecordsToRentRecordDTOsFromRented(rentRecordRepository.findByAccountIdOnlyRented(id)), HttpStatus.OK);
        }
        return new ResponseEntity<>(rentRecordMapper.rentRecordsToRentRecordDTOsFromRented(rentRecordRepository.findByAccountId(id)), HttpStatus.OK);
    }

    @GetMapping("/book-record/{id}")
    @JsonView(RentedBookView.class)
    public ResponseEntity<List<RentRecordDTO>> getRentRecordsByBookRecordId(@PathVariable Long id,
                                                                            @RequestParam(value = "only-rented", required = false) boolean onlyRented) {
        log.info("Getting all rent records by book record id");
        if (onlyRented) {
            return new ResponseEntity<>(rentRecordMapper.rentRecordsToRentRecordDTOsFromRented(rentRecordRepository.findByBookRecordIdOnlyRented(id)), HttpStatus.OK);
        }
        return new ResponseEntity<>(rentRecordMapper.rentRecordsToRentRecordDTOsFromRented(rentRecordRepository.findByBookRecordId(id)), HttpStatus.OK);
    }

    @GetMapping("/book/{id}")
    @JsonView(RentedBookView.class)
    public ResponseEntity<List<RentRecordDTO>> getRentRecordsByBookId(@PathVariable Long id,
                                                                      @RequestParam(value = "only-rented", required = false) boolean onlyRented) {
        log.info("Getting all rent records by book id");
        if (onlyRented) {
            return new ResponseEntity<>(rentRecordMapper.rentRecordsToRentRecordDTOsFromRented(rentRecordRepository.findByBookIdOnlyRented(id)), HttpStatus.OK);
        }
        return new ResponseEntity<>(rentRecordMapper.rentRecordsToRentRecordDTOsFromRented(rentRecordRepository.findByBookId(id)), HttpStatus.OK);
    }

    @PatchMapping("/{id}/overdue-days-paid=true")
    public ResponseEntity<?> overdueDaysPaid(@PathVariable Long id) {
        log.info("Overdue days paid");
        Optional<RentRecord> rentRecordOptional = rentRecordRepository.findById(id);
        if (rentRecordOptional.isEmpty()) {
            return new ResponseEntity<>(new ApiErrorDTO(HttpStatus.NOT_FOUND.value(), "Rent record is not found"), HttpStatus.NOT_FOUND);
        }
        RentRecord rentRecord = rentRecordOptional.get();
        if (rentRecord.getOverdueDays() < 1) {
            return new ResponseEntity<>(new ApiErrorDTO(HttpStatus.BAD_REQUEST.value(), "This rent record does not need overdue payment"), HttpStatus.BAD_REQUEST);
        }
        if (rentRecord.getActualReturnTime() == null) {
            return new ResponseEntity<>(new ApiErrorDTO(HttpStatus.BAD_REQUEST.value(), "This book record id: " + rentRecord.getBookRecord().getId() + " is not returned yet"), HttpStatus.BAD_REQUEST);
        }
        rentRecord.setOverdueDaysPaid(true);
        rentRecordRepository.save(rentRecord);
        return new ResponseEntity<>(rentRecordMapper.rentRecordToRentRecordDTOFromRented(rentRecord), HttpStatus.OK);
    }
}
