package uz.imirsaburov.demotesting.models.country;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Data
public class CountryFilter {
    @Min(value = 0)
    private int page;

    @Min(value = 1)
    @Max(value = 20)
    private int size;

    private String name;

}
