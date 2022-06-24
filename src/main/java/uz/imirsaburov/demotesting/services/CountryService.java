package uz.imirsaburov.demotesting.services;

import org.springframework.data.domain.Page;
import uz.imirsaburov.demotesting.models.country.CountryDTO;
import uz.imirsaburov.demotesting.models.country.CountryFilter;
import uz.imirsaburov.demotesting.models.country.CreateCountryDTO;
import uz.imirsaburov.demotesting.models.country.UpdateCountryDTO;

public interface CountryService {

    void create(CreateCountryDTO dto);

    void update(Long id, UpdateCountryDTO dto);

    void delete(Long id);

    CountryDTO getById(Long id);

    Page<CountryDTO> getList(CountryFilter filter);

}
