package com.example.test.domain.repository;

import com.example.test.domain.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    @Override
    List<Account> findAll();

    @Query("SELECT a FROM Account a " +
            "LEFT JOIN a.rentRecords rr " +
            "WHERE rr.actualReturnTime IS NULL")
    List<Account> findAllWithRentedBooks();

}
