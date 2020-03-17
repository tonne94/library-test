package com.example.test.interfaces.web.rest;

import com.example.test.application.AccountService;
import com.example.test.domain.model.Account;
import com.example.test.domain.model.BookRecord;
import com.example.test.domain.model.RentRecord;
import com.example.test.domain.repository.AccountRepository;
import com.example.test.domain.repository.BookRecordRepository;
import com.example.test.domain.repository.RentRecordRepository;
import com.example.test.dto.AccountDTO;
import com.example.test.dto.ApiErrorDTO;
import com.example.test.dto.DocumentDTO;
import com.example.test.interfaces.web.rest.JsonView.AccountView;
import com.example.test.interfaces.web.rest.mapper.AccountMapper;
import com.fasterxml.jackson.annotation.JsonView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    Logger log = LoggerFactory.getLogger(AccountController.class);

    private AccountRepository accountRepository;
    private RentRecordRepository rentRecordRepository;
    private BookRecordRepository bookRecordRepository;
    private AccountMapper accountMapper;
    private AccountService accountService;

    public AccountController(AccountRepository accountRepository,
                             AccountMapper accountMapper,
                             AccountService accountService,
                             BookRecordRepository bookRecordRepository,
                             RentRecordRepository rentRecordRepository) {
        this.accountRepository = accountRepository;
        this.accountMapper = accountMapper;
        this.accountService = accountService;
        this.bookRecordRepository = bookRecordRepository;
        this.rentRecordRepository = rentRecordRepository;
    }

    @GetMapping
    @JsonView(AccountView.class)
    public ResponseEntity<List<AccountDTO>> getAccounts(@RequestParam(value = "only-rented", required = false) boolean onlyRentedBooks,
                                                        @RequestParam(value = "only-overdue", required = false) boolean onlyOverdue) {
        log.info("Getting all accounts");
        if (onlyRentedBooks) {
            return new ResponseEntity<>(accountService.findAllOnlyRentedBooks(onlyOverdue), HttpStatus.OK);
        }
        return new ResponseEntity<>(accountService.findAll(onlyOverdue), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @JsonView(AccountView.class)
    public ResponseEntity<AccountDTO> getAccount(@PathVariable Long id, @RequestParam(value = "only-rented", required = false) boolean onlyRentedBooks) {
        log.info("Get account by id: " + id);
        if (onlyRentedBooks) {
            AccountDTO accountDTO = accountService.findByIdOnlyRentedBooks(id);
            if(accountDTO!=null){
                return new ResponseEntity<>(accountDTO, HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Optional<Account> optionalAccount = accountRepository.findById(id);
        return optionalAccount.map(account -> new ResponseEntity<>(accountMapper.accountToAccountDTOFromAccount(account), HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/image")
    @JsonView(AccountView.class)
    public ResponseEntity<AccountDTO> createAccountUsingImage(@RequestParam("file") MultipartFile file) {
        log.info("Creating account using image");
        Optional<Account> result = accountService.createAccountWithImage(file);
        return new ResponseEntity<>(accountMapper.accountToAccountDTOFromAccount(result.get()), HttpStatus.OK);
    }

    @PostMapping("/with-document=true")
    @JsonView(AccountView.class)
    public ResponseEntity<AccountDTO> createAccountUsingDocumentDTO(@Valid @RequestBody DocumentDTO documentDTO) {
        log.info("Creating account using documentDTO");
        Optional<Account> result = accountService.createAccountWithDocumentDTO(documentDTO);
        return new ResponseEntity<>(accountMapper.accountToAccountDTOFromAccount(result.get()), HttpStatus.OK);
    }

    @PostMapping
    @JsonView(AccountView.class)
    public ResponseEntity<AccountDTO> createAccount(@Valid @RequestBody AccountDTO accountDTO) {
        log.info("Creating account");
        Optional<Account> result = accountService.createAccount(accountMapper.accountDTOToAccount(accountDTO));
        return new ResponseEntity<>(accountMapper.accountToAccountDTOFromAccount(result.get()), HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    @JsonView(AccountView.class)
    public ResponseEntity<AccountDTO> updateAccount(@Valid @RequestBody AccountDTO accountDTO) {
        log.info("Updating account with id: " + accountDTO.getId());
        Account result = accountService.updateAccount(accountMapper.accountDTOToAccount(accountDTO));
        return new ResponseEntity<>(accountMapper.accountToAccountDTOFromAccount(result), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAccount(@PathVariable Long id) {
        log.info("Deleting account with id: " + id);
        accountRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/{accountId}/rent-book-record/{bookRecordId}")
    @JsonView(AccountView.class)
    public ResponseEntity<?> rentBook(@PathVariable Long accountId, @PathVariable Long bookRecordId) {
        log.info("Renting a book with account id: " + accountId + "and book record id: " + bookRecordId);

        Optional<Account> accountOptional = accountRepository.findById(accountId);
        if (accountOptional.isEmpty()) {
            log.error("Account not found for id: " + accountId);
            return new ResponseEntity<>(new ApiErrorDTO(HttpStatus.BAD_REQUEST.value(), "Account is not found"), HttpStatus.BAD_REQUEST);
        }
        Optional<BookRecord> bookRecordOptional = bookRecordRepository.findById(bookRecordId);
        if (bookRecordOptional.isEmpty()) {
            log.error("Book record not found for id: " + bookRecordId);
            return new ResponseEntity<>(new ApiErrorDTO(HttpStatus.BAD_REQUEST.value(), "Book record is not found"), HttpStatus.BAD_REQUEST);
        }
        if (!bookRecordRepository.findIfBookRecordAvailable(bookRecordId)) {
            log.error("Book record is already rented, book record id: " + bookRecordId);
            return new ResponseEntity<>(new ApiErrorDTO(HttpStatus.BAD_REQUEST.value(), "Book record is already rented"), HttpStatus.BAD_REQUEST);
        }
        if (bookRecordOptional.get().isInvalid()) {
            log.error("Book record is invalid, id: " + bookRecordId);
            return new ResponseEntity<>(new ApiErrorDTO(HttpStatus.BAD_REQUEST.value(), "Book record is invalid."), HttpStatus.BAD_REQUEST);
        }

        Optional<Account> result = accountService.rentBook(accountOptional.get(), bookRecordOptional.get());
        return new ResponseEntity<>(accountMapper.accountToAccountDTOFromAccount(result.get()), HttpStatus.OK);
    }

    @PostMapping("/{accountId}/return-book/{bookRecordId}")
    @JsonView(AccountView.class)
    public ResponseEntity<?> returnBook(@PathVariable Long accountId, @PathVariable Long bookRecordId) {
        log.info("Returning a book with account id: " + accountId + "and book record id: " + bookRecordId);
        Optional<RentRecord> rentRecordOptional = rentRecordRepository.findByAccountIdAndBookRecordIdAndActualReturnTimeIsNull(accountId, bookRecordId);
        if (rentRecordOptional.isEmpty()) {
            log.error("Rent record for account id: " + accountId + " and book record id: " + bookRecordId + " not found");
            return new ResponseEntity<>(new ApiErrorDTO(HttpStatus.NOT_FOUND.value(), "Rent record is not found"), HttpStatus.NOT_FOUND);
        }
        Account result = accountService.returnBook(rentRecordOptional.get());
        return new ResponseEntity<>(accountMapper.accountToAccountDTOFromAccount(result), HttpStatus.OK);
    }
}
