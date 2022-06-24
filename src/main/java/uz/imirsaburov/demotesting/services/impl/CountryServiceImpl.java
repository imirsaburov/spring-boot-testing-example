package uz.imirsaburov.demotesting.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import uz.imirsaburov.demotesting.entities.CountryEntity;
import uz.imirsaburov.demotesting.exceptions.country.CountryNameExistException;
import uz.imirsaburov.demotesting.exceptions.country.CountryNotFoundException;
import uz.imirsaburov.demotesting.models.country.CountryDTO;
import uz.imirsaburov.demotesting.models.country.CountryFilter;
import uz.imirsaburov.demotesting.models.country.CreateCountryDTO;
import uz.imirsaburov.demotesting.models.country.UpdateCountryDTO;
import uz.imirsaburov.demotesting.repositories.CountryRepository;
import uz.imirsaburov.demotesting.services.CountryService;

@Service(value = "country-service")
@RequiredArgsConstructor
public class CountryServiceImpl implements CountryService {

    private final CountryRepository repository;

    private CountryDTO toDTO(CountryEntity entity) {
        CountryDTO dto = new CountryDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());

        return dto;
    }

    private CountryEntity getEntityById(Long id) {
        return repository.findById(id).orElseThrow(() -> new CountryNotFoundException(id));
    }

    @Override
    public void create(CreateCountryDTO dto) {
        String name = dto.getName();

        if (repository.existsByNameContainingIgnoreCase(name))
            throw new CountryNameExistException(name);

        CountryEntity entity = new CountryEntity();
        entity.setName(name);
        repository.save(entity);
    }

    @Override
    public void update(Long id, UpdateCountryDTO dto) {
        String name = dto.getName();

        if (repository.existsByNameContainingIgnoreCaseAndId(name, id))
            throw new CountryNameExistException(name);

        CountryEntity entity = new CountryEntity();
        entity.setName(name);
        repository.save(entity);
    }

    @Override
    public void delete(Long id) {
        CountryEntity entity = getEntityById(id);
        repository.delete(entity);
    }

    @Override
    public CountryDTO getById(Long id) {
        return toDTO(getEntityById(id));
    }

    @Override
    public Page<CountryDTO> getList(CountryFilter filter) {
        final PageRequest pageRequest = PageRequest.of(filter.getPage(), filter.getSize());

        Page<CountryEntity> countryEntityPage;

        String searchName = filter.getName();
        boolean searchNameCondition = searchName != null && !searchName.isBlank();

        if (searchNameCondition)
            countryEntityPage = repository.findAllByNameContainingIgnoreCase(searchName, pageRequest);
        else
            countryEntityPage = repository.findAll(pageRequest);

        return countryEntityPage.map(this::toDTO);
    }
}
