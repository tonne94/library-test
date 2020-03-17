package com.example.test.domain.enums;

import lombok.Getter;

@Getter
public enum ContactType {

    EMAIL(0, "E-mail"),
    PHONE(1, "Phone"),
    MOBILE_PHONE(2, "Mobile phone");

    private final Integer code;
    private final String displayName;

    ContactType(Integer code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

}
