package com.example.test.dto;

import com.example.test.interfaces.web.rest.JsonView.AccountView;
import com.example.test.interfaces.web.rest.JsonView.AuthorView;
import com.example.test.interfaces.web.rest.JsonView.BookView;
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
public class AuthorDTO implements Serializable {

    @JsonView({BookView.class, AccountView.class, AuthorView.class})
    private Long id;

    @NotBlank(message = "Name should not be blank")
    @Pattern(regexp = "[A-Za-z]*$", message = "Name should contain only letters")
    @JsonView({BookView.class, AccountView.class, AuthorView.class})
    private String name;

    @NotBlank(message = "Surname should not be blank")
    @Pattern(regexp = "[A-Za-z]*$", message = "Surname should contain only letters")
    @JsonView({BookView.class, AccountView.class, AuthorView.class})
    private String surname;

    @JsonView({AuthorView.class})
    private List<BookDTO> books = new ArrayList<>();
    ;
}
