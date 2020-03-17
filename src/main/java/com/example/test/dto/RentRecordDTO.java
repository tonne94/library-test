package com.example.test.dto;

import com.example.test.interfaces.web.rest.JsonView.AccountView;
import com.example.test.interfaces.web.rest.JsonView.RentedBookView;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

;

@Getter
@Setter
public class RentRecordDTO implements Serializable {

    @JsonView({AccountView.class, RentedBookView.class})
    private Long id;

    @JsonView({AccountView.class, RentedBookView.class})
    private BookRecordDTO bookRecord;

    @JsonView({RentedBookView.class})
    private AccountDTO account;

    @JsonView({AccountView.class, RentedBookView.class})
    private LocalDateTime rentTime;

    @JsonView({AccountView.class, RentedBookView.class})
    private LocalDateTime returnTime;

    @JsonView({AccountView.class, RentedBookView.class})
    private LocalDateTime actualReturnTime;

    @JsonView({AccountView.class, RentedBookView.class})
    private boolean overdueDaysPaid = false;

    @JsonView({AccountView.class, RentedBookView.class})
    private Long overdueDays;
}
