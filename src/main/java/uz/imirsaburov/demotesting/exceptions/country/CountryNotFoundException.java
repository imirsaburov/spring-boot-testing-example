package uz.imirsaburov.demotesting.exceptions.country;

import org.springframework.http.HttpStatus;
import uz.imirsaburov.demotesting.enums.ErrorCode;
import uz.imirsaburov.demotesting.exceptions.BaseException;

public class CountryNotFoundException extends BaseException {

    public CountryNotFoundException(Long id) {
        super("Country not found : " + id, HttpStatus.NOT_FOUND, ErrorCode.COUNTRY_NOT_FOUND);
    }
}
