package uz.imirsaburov.demotesting.exceptions.country;

import org.springframework.http.HttpStatus;
import uz.imirsaburov.demotesting.enums.ErrorCode;
import uz.imirsaburov.demotesting.exceptions.BaseException;

public class CountryNameExistException extends BaseException {

    public CountryNameExistException(String name) {
        super("Country name exist : ".concat(name), HttpStatus.NOT_FOUND, ErrorCode.COUNTRY_NAME_EXIST);
    }
}
