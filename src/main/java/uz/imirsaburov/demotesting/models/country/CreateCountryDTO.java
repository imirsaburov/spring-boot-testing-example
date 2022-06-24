package uz.imirsaburov.demotesting.models.country;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class CreateCountryDTO {

    @NotBlank(message = "The name field must not contain only whitespace!")
    @Pattern(regexp = "[A-z\\w]{2,30}", message = "The name field must contain only letters " +
            "and The length of the country name must be between two and thirty!")
    private String name;
}
