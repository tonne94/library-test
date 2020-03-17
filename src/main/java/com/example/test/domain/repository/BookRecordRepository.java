package com.example.test.domain.repository;

import com.example.test.domain.model.BookRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRecordRepository extends JpaRepository<BookRecord, Long> {

    @Override
    List<BookRecord> findAll();

    @Query(nativeQuery = true,
            value = "SELECT CASE WHEN COUNT(br)>0 THEN false ELSE true END FROM BOOK_RECORD br " +
                    "WHERE br.id = :bookRecordId " +
                    "AND EXISTS ( " +
                    "   SELECT 1 FROM rent_record rr WHERE rr.actual_return_time IS NULL AND rr.book_record_id = :bookRecordId " +
                    ")")
    boolean findIfBookRecordAvailable(@Param("bookRecordId") Long bookRecordId);

}
