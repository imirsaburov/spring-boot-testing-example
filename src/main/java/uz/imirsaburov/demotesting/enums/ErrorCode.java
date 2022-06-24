package uz.imirsaburov.demotesting.enums;

import lombok.Getter;

@Getter
public enum ErrorCode {
    //country prefix 00002*
    COUNTRY_NOT_FOUND("00002001"),
    COUNTRY_NAME_EXIST("00002002"),
    ;
    private String code;

    ErrorCode(String code) {
        this.code = code;
    }
}
