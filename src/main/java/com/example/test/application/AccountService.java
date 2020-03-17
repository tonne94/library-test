package com.example.test.application;

import com.example.test.domain.model.Account;
import com.example.test.domain.model.BookRecord;
import com.example.test.domain.model.RentRecord;
import com.example.test.dto.AccountDTO;
import com.example.test.dto.DocumentDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface AccountService {

    Optional<Account> rentBook(Account account, BookRecord bookRecord);

    Account returnBook(RentRecord rentRecord);

    List<AccountDTO> findAll(boolean onlyOverdue);

    List<AccountDTO> findAllOnlyRentedBooks(boolean onlyOverdue);

    AccountDTO findByIdOnlyRentedBooks(Long id);

    Optional<Account> createAccount(Account account);

    Optional<Account> createAccountWithDocumentDTO(DocumentDTO documentDTO);

    Optional<Account> createAccountWithImage(MultipartFile file);

    Account updateAccount(Account account);
}
