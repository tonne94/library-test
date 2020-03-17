package com.example.test.dto;

import com.example.test.domain.model.Account;
import com.example.test.domain.model.RentRecord;
import com.example.test.interfaces.web.rest.JsonView.AccountView;
import com.example.test.interfaces.web.rest.JsonView.RentedBookView;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class AccountDTO implements Serializable, Comparable<AccountDTO> {

    @JsonView({AccountView.class, RentedBookView.class})
    private Long id;

    @NotBlank(message = "Name should not be blank")
    @Pattern(regexp = "[A-Za-z]*$", message = "Name should contain only letters")
    @JsonView({AccountView.class, RentedBookView.class})
    private String name;

    @NotBlank(message = "Surname should not be blank")
    @Pattern(regexp = "[A-Za-z]*$", message = "Name should contain only letters")
    @JsonView({AccountView.class, RentedBookView.class})
    private String surname;

    @JsonView(AccountView.class)
    private List<RentRecordDTO> rentRecords = new ArrayList<>();

    @JsonView(AccountView.class)
    private List<ContactDTO> contacts = new ArrayList<>();

    @JsonView({AccountView.class, RentedBookView.class})
    private boolean isValid;

    @Override
    public int compareTo(AccountDTO accountDTO) {
        Long overdueDaysSum = rentRecords.stream().filter(rentRecord -> rentRecord.getActualReturnTime() == null).mapToLong(RentRecordDTO::getOverdueDays).sum();
        Long overdueDaysSumAccount = accountDTO.getRentRecords().stream().filter(rentRecord -> rentRecord.getActualReturnTime() == null).mapToLong(RentRecordDTO::getOverdueDays).sum();
        return overdueDaysSum.compareTo(overdueDaysSumAccount);
    }
}
