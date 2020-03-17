package com.example.test.dto;

import com.example.test.interfaces.web.rest.JsonView.AccountView;
import com.example.test.interfaces.web.rest.JsonView.AuthorView;
import com.example.test.interfaces.web.rest.JsonView.BookView;
import com.example.test.interfaces.web.rest.JsonView.RentedBookView;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class BookDTO implements Serializable {

    @JsonView({BookView.class, AccountView.class, AuthorView.class, RentedBookView.class})
    private Long id;

    @NotBlank(message = "Title should not be blank")
    @JsonView({BookView.class, AccountView.class, AuthorView.class, RentedBookView.class})
    private String title;

    @JsonView({BookView.class, AccountView.class})
    private List<AuthorDTO> authors = new ArrayList<>();

    @JsonView(BookView.class)
    private List<BookRecordDTO> bookRecords = new ArrayList<>();

    @JsonView(BookView.class)
    private int copies;

    @JsonView(BookView.class)
    private int copiesAvailable;
}
