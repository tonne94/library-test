package com.example.test.dto;

import com.example.test.interfaces.web.rest.JsonView.RootView;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public class ApiErrorDTO implements Serializable {

    @JsonView(RootView.class)
    private int errorCode;

    @JsonView(RootView.class)
    private String message;
}
