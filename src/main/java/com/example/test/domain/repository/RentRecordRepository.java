package com.example.test.domain.repository;

import com.example.test.domain.model.RentRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RentRecordRepository extends JpaRepository<RentRecord, Long> {

    @Override
    List<RentRecord> findAll();

    @Query(nativeQuery = true,
            value = "SELECT rr.* FROM rent_record rr " +
                    "WHERE rr.actual_return_time IS NULL")
    List<RentRecord> findAllOnlyRented();

    @Query(nativeQuery = true,
            value = "SELECT rr.* FROM rent_record rr " +
                    "WHERE rr.account_id = :accountId  " +
                    "AND rr.book_record_id = :bookRecordId " +
                    "AND rr.actual_return_time IS NULL")
    Optional<RentRecord> findByAccountIdAndBookRecordIdAndActualReturnTimeIsNull(@Param("accountId") Long accountId, @Param("bookRecordId") Long bookRecordId);

    @Query(nativeQuery = true,
            value = "SELECT rr.* FROM rent_record rr " +
                    "WHERE rr.book_record_id = :bookRecordId " +
                    "AND rr.actual_return_time IS NULL")
    Optional<RentRecord> findByBookRecordIdAndActualReturnTimeIsNull(@Param("bookRecordId") Long bookRecordId);

    @Query(nativeQuery = true,
            value = "SELECT rr.* FROM rent_record rr " +
                    "WHERE rr.account_id = :accountId")
    List<RentRecord> findByAccountId(@Param("accountId") Long accountId);

    @Query(nativeQuery = true,
            value = "SELECT rr.* FROM rent_record rr " +
                    "WHERE rr.account_id = :accountId " +
                    "AND rr.actual_return_time IS NULL")
    List<RentRecord> findByAccountIdOnlyRented(@Param("accountId") Long accountId);

    @Query(nativeQuery = true,
            value = "SELECT rr.* FROM rent_record rr " +
                    "WHERE rr.book_record_id = :bookRecordId")
    List<RentRecord> findByBookRecordId(@Param("bookRecordId") Long bookRecordId);

    @Query(nativeQuery = true,
            value = "SELECT rr.* FROM rent_record rr " +
                    "WHERE rr.book_record_id = :bookRecordId " +
                    "AND rr.actual_return_time IS NULL")
    List<RentRecord> findByBookRecordIdOnlyRented(@Param("bookRecordId") Long bookRecordId);

    @Query(nativeQuery = true,
            value = "SELECT * FROM rent_record rr " +
                    "LEFT JOIN book_record br ON br.id = rr.book_record_id " +
                    "WHERE br.book_id = :bookId")
    List<RentRecord> findByBookId(@Param("bookId") Long bookId);

    @Query(nativeQuery = true,
            value = "SELECT * FROM rent_record rr " +
                    "LEFT JOIN book_record br ON br.id = rr.book_record_id " +
                    "WHERE br.book_id = :bookId " +
                    "AND rr.actual_return_time IS NULL")
    List<RentRecord> findByBookIdOnlyRented(@Param("bookId") Long bookId);
}
