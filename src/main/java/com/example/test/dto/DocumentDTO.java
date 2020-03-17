package com.example.test.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class DocumentDTO {

    @NotBlank(message = "recognizerType should not be blank")
    private String recognizerType;

    @NotBlank(message = "imageBase64 should not be blank")
    private String imageBase64;
}
