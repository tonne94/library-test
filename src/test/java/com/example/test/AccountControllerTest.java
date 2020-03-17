package com.example.test;

import com.example.test.domain.enums.ContactType;
import com.example.test.domain.model.Account;
import com.example.test.domain.model.Author;
import com.example.test.domain.model.Book;
import com.example.test.domain.model.BookRecord;
import com.example.test.domain.model.Contact;
import com.example.test.domain.repository.AccountRepository;
import com.example.test.domain.repository.BookRecordRepository;
import com.example.test.domain.repository.BookRepository;
import com.example.test.domain.repository.RentRecordRepository;
import com.example.test.dto.AccountDTO;
import com.example.test.dto.ApiErrorDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.transaction.Transactional;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest(classes = {LibraryApplication.class})
@Transactional
public class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private RentRecordRepository rentRecordRepository;

    @Autowired
    private BookRecordRepository bookRecordRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @Transactional
    public void testRentingBook() throws Exception {

        Book book = createBook("BookOne");

        Account account = createAccount("One");

        MvcResult result = this.mockMvc.perform(
                post("/accounts/" + account.getId() + "/rent-book/" + book.getBookRecords().get(0).getId())
        ).andReturn();

        AccountDTO accountDTO = objectMapper.readerFor(AccountDTO.class).readValue(result.getResponse().getContentAsByteArray());
        assertEquals(1, accountDTO.getRentRecords().stream().filter(rentRecordDTO -> rentRecordDTO.getActualReturnTime() == null).count());
    }

    @Test
    @Transactional
    public void testRentingAndReturningBook() throws Exception {

        Book book = createBook("BookOne");

        Account account = createAccount("One");

        this.mockMvc.perform(
                post("/accounts/" + account.getId() + "/rent-book/" + book.getBookRecords().get(0).getId())
        ).andReturn();

        MvcResult result = this.mockMvc.perform(
                post("/accounts/" + account.getId() + "/return-book/" + book.getBookRecords().get(0).getId())
        ).andReturn();

        AccountDTO accountDTO = objectMapper.readerFor(AccountDTO.class).readValue(result.getResponse().getContentAsByteArray());
        assertEquals(0, accountDTO.getRentRecords().stream().filter(rentRecordDTO -> rentRecordDTO.getActualReturnTime() == null).count());
    }

    /*
        This test is testing if we are trying to call a return book endpoint while book was not even rented before.
        Should return 400 error code.
     */
    @Test
    @Transactional
    public void testReturningBook_ShouldGetErrorThatBookIsNotRented() throws Exception {

        Book book = createBook("BookOne");

        Account account = createAccount("One");

        MvcResult result = this.mockMvc.perform(
                post("/accounts/" + account.getId() + "/return-book/" + book.getBookRecords().get(0).getId())
        ).andExpect(status().isNotFound()).andReturn();

        ApiErrorDTO apiErrorDTO = objectMapper.readerFor(ApiErrorDTO.class).readValue(result.getResponse().getContentAsByteArray());
        assertEquals(404, apiErrorDTO.getErrorCode());
        assertEquals("Rent record is not found", apiErrorDTO.getMessage());
    }

    private Account createAccount(String accountName) {
        Account account = new Account();
        account.setName("Account" + accountName + "Name");
        account.setSurname("Account" + accountName + "Surname");

        Contact contact = new Contact();
        contact.setContactType(ContactType.EMAIL);
        contact.setContact("account" + accountName.toLowerCase() + "@email.com");
        contact.setAccount(account);
        account.setContacts(Lists.newArrayList(contact));

        return accountRepository.save(account);
    }

    private Book createBook(String bookName) {
        Book book = new Book();
        book.setTitle(bookName);
        Author author = new Author();
        author.setName("AuthorName" + bookName);
        author.setSurname("AuthorSurname" + bookName);
        book.setAuthors(Set.of(author));

        BookRecord bookRecord = new BookRecord();
        bookRecord.setBook(book);
        book.getBookRecords().add(bookRecord);
        book = bookRepository.save(book);

        return book;
    }
}
