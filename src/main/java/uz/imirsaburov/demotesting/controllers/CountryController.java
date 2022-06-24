package uz.imirsaburov.demotesting.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import uz.imirsaburov.demotesting.models.country.CountryDTO;
import uz.imirsaburov.demotesting.models.country.CountryFilter;
import uz.imirsaburov.demotesting.models.country.CreateCountryDTO;
import uz.imirsaburov.demotesting.models.country.UpdateCountryDTO;
import uz.imirsaburov.demotesting.services.CountryService;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@RestController(value = "country-controller")
@RequestMapping("/v1/country")
@RequiredArgsConstructor
@Validated
public class CountryController {

    private final CountryService countryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createCountry(@Valid @RequestBody CreateCountryDTO createCountryDTO) {
        countryService.create(createCountryDTO);
    }

    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public void createCountry(@Min(1) @PathVariable Long id, @Valid @RequestBody UpdateCountryDTO updateCountryDTO) {
        countryService.update(id, updateCountryDTO);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteCountry(@Min(1) @PathVariable Long id) {
        countryService.delete(id);
    }

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public CountryDTO getCountryById(@Min(1) @PathVariable Long id) {
        return countryService.getById(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<CountryDTO> getCountryById(@Valid CountryFilter filter) {
        return countryService.getList(filter);
    }
}
