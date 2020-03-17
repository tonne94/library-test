package com.example.test.dto;

import com.example.test.interfaces.web.rest.JsonView.AccountView;
import com.example.test.interfaces.web.rest.JsonView.BookView;
import com.example.test.interfaces.web.rest.JsonView.RentedBookView;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class BookRecordDTO implements Serializable {

    @JsonView({BookView.class, AccountView.class, RentedBookView.class})
    private Long id;

    @JsonView({AccountView.class, RentedBookView.class})
    private BookDTO book;

    @JsonView(BookView.class)
    private List<RentRecordDTO> rentRecords = new ArrayList<>();

    @JsonView({BookView.class, AccountView.class, RentedBookView.class})
    private boolean damaged = false;

    @JsonView({BookView.class, AccountView.class, RentedBookView.class})
    private boolean invalid = false;

    @JsonView({BookView.class, AccountView.class, RentedBookView.class})
    private boolean isAvailable = true;
}
