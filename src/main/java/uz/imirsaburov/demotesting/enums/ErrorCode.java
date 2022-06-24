package uz.imirsaburov.demotesting.enums;

import lombok.Getter;

@Getter
public enum ErrorCode {
    // global prefix 00000*
    GLOBAL("00000001"),
    METHOD_NOT_VALID("00000002"),

    //country prefix 00002*
    COUNTRY_NOT_FOUND("00002001"),
    COUNTRY_NAME_EXIST("00002002"),
    ;
    private String code;

    ErrorCode(String code) {
        this.code = code;
    }
}
