package com.example.test.dto;

import com.example.test.domain.enums.ContactType;
import com.example.test.interfaces.web.rest.JsonView.AccountView;
import com.example.test.interfaces.web.rest.JsonView.RentedBookView;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class ContactDTO {

    @JsonView({AccountView.class, RentedBookView.class})
    private Long id;

    private AccountDTO account;

    @JsonView({AccountView.class, RentedBookView.class})
    @NotBlank(message = "ContactType should not be blank")
    private ContactType contactType;

    @JsonView({AccountView.class, RentedBookView.class})
    @NotBlank(message = "Contact should not be blank")
    private String contact;
}
