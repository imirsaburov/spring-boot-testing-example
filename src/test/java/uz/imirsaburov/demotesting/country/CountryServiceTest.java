package uz.imirsaburov.demotesting.country;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import uz.imirsaburov.demotesting.entities.CountryEntity;
import uz.imirsaburov.demotesting.exceptions.country.CountryNameExistException;
import uz.imirsaburov.demotesting.exceptions.country.CountryNotFoundException;
import uz.imirsaburov.demotesting.models.country.CountryDTO;
import uz.imirsaburov.demotesting.models.country.CountryFilter;
import uz.imirsaburov.demotesting.models.country.CreateCountryDTO;
import uz.imirsaburov.demotesting.models.country.UpdateCountryDTO;
import uz.imirsaburov.demotesting.repositories.CountryRepository;
import uz.imirsaburov.demotesting.services.CountryService;
import uz.imirsaburov.demotesting.services.impl.CountryServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CountryServiceTest {

    @Mock
    private CountryRepository countryRepository;

    private CountryService underTest;

    @BeforeEach
    void setUp() {
        this.underTest = new CountryServiceImpl(countryRepository);
    }

    @Test
    @DisplayName("countryServiceTest canGetCountriesWithoutNameSearch()")
    void canGetCountriesWithoutNameSearch() {

        //given
        CountryFilter filter = new CountryFilter();
        filter.setPage(0);
        filter.setSize(10);

        PageRequest pageRequest = PageRequest.of(filter.getPage(), filter.getSize());

        List<CountryEntity> countryEntityList = new ArrayList<>();

        String[] countryNameArray = {
                "Uzbekistan",
                "Usa"
        };

        long i = 1L;
        for (String countryName : countryNameArray)
            countryEntityList.add(new CountryEntity(i++, countryName));

        given(countryRepository.findAll(pageRequest))
                .willReturn(new PageImpl<>(countryEntityList, pageRequest, countryEntityList.size()));

        //when
        underTest.getList(filter);

        //then
        verify(countryRepository).findAll(pageRequest);
    }

    @Test
    void canGetCountriesWithNameSearch() {

        //given

        final String searchName = "Uzbekistan";
        CountryFilter filter = new CountryFilter();
        filter.setPage(0);
        filter.setSize(10);
        filter.setName(searchName);

        PageRequest pageRequest = PageRequest.of(filter.getPage(), filter.getSize());

        List<CountryEntity> countryEntityList = new ArrayList<>();

        String[] countryNameArray = {
                "Uzbekistan"
        };

        long i = 1L;
        for (String countryName : countryNameArray)
            countryEntityList.add(new CountryEntity(i++, countryName));

        given(countryRepository.findAllByNameContainingIgnoreCase(searchName, pageRequest))
                .willReturn(new PageImpl<>(countryEntityList, pageRequest, countryEntityList.size()));

        //when
        underTest.getList(filter);

        //then
        verify(countryRepository).findAllByNameContainingIgnoreCase(searchName, pageRequest);
    }

    @Test
    void canGetCountryById() {

        //given

        final Long id = 1L;
        final String name = "Uzbeksitan";
        final CountryEntity entity = new CountryEntity(id, name);
        final CountryDTO countryDTO = new CountryDTO();
        countryDTO.setId(id);
        countryDTO.setName(name);

        given(countryRepository.findById(id))
                .willReturn(Optional.of(entity));

        //when
        CountryDTO result = underTest.getById(id);

        //then
        verify(countryRepository).findById(id);
        assertThat(result).isEqualTo(countryDTO);
    }

    @Test
    void willThrowWhenGetCountryByIdNotFoundException() {

        //given
        final Long id = 1L;

        given(countryRepository.findById(id))
                .willReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> underTest.getById(id))
                .isInstanceOf(CountryNotFoundException.class);
    }

    @Test
    void itShouldCreateCountry() {

        //given
        final String name = "Uzbekistan";
        final CreateCountryDTO createCountryDTO = new CreateCountryDTO();
        createCountryDTO.setName(name);

        given(countryRepository.existsByNameContainingIgnoreCase(name)).willReturn(false);

        //when
        underTest.create(createCountryDTO);

        //then
        verify(countryRepository).save(any());
    }

    @Test
    void itShouldThrowOnCountryCreateCountryNameAlreadyExistException() {

        //given
        final String name = "Uzbekistan";
        final CreateCountryDTO dto = new CreateCountryDTO();
        dto.setName(name);

        given(countryRepository.existsByNameContainingIgnoreCase(name)).willReturn(true);

        //then
        //when
        assertThatThrownBy(() -> underTest.create(dto))
                .isInstanceOf(CountryNameExistException.class);

        verify(countryRepository, never()).save(any());
    }

    @Test
    void itShouldUpdateCountry() {

        //given
        final String name = "Uzbekistan";
        final Long id = 1L;
        final UpdateCountryDTO updateCountryDTO = new UpdateCountryDTO();
        updateCountryDTO.setName(name);

        final CountryEntity entity = new CountryEntity(id, name.concat(" old"));

        given(countryRepository.existsByNameContainingIgnoreCaseAndIdNot(name, id)).willReturn(false);
        given(countryRepository.findById(id)).willReturn(Optional.of(entity));

        //when
        underTest.update(id, updateCountryDTO);
        entity.setName(name);

        //then
        verify(countryRepository).save(entity);
    }

    @Test
    void itShouldThrowOnCountryUpdateCountryNotFoundException() {

        //given
        final String name = "Uzbekistan";
        final Long id = 1L;
        final UpdateCountryDTO updateCountryDTO = new UpdateCountryDTO();
        updateCountryDTO.setName(name);

        given(countryRepository.existsByNameContainingIgnoreCaseAndIdNot(name, id)).willReturn(false);
        given(countryRepository.findById(id)).willReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> underTest.update(id, updateCountryDTO))
                .isInstanceOf(CountryNotFoundException.class);

        verify(countryRepository, never()).save(any());
    }

    @Test
    void itShouldThrowOnCountryUpdateCountryNameAlreadyExistException() {

        //given
        final String name = "Uzbekistan";
        final Long id = 1L;
        final UpdateCountryDTO dto = new UpdateCountryDTO();
        dto.setName(name);

        given(countryRepository.existsByNameContainingIgnoreCaseAndIdNot(name, id)).willReturn(true);

        //then
        //when
        assertThatThrownBy(() -> underTest.update(id, dto))
                .isInstanceOf(CountryNameExistException.class);

        verify(countryRepository, never()).save(any());

    }

    @Test
    void itShouldDeleteCountry() {
        //given
        final Long id = 1L;
        final String name = "Uzbekistan";
        final CountryEntity entity = new CountryEntity(id, name);

        given(countryRepository.findById(id)).willReturn(Optional.of(entity));

        //when
        underTest.delete(id);

        //then
        verify(countryRepository).delete(entity);
    }

    @Test
    void willThrowWhenCountryDeleteCountryNotException() {
        //given
        final Long id = 1L;

        given(countryRepository.findById(id)).willReturn(Optional.empty());

        //when
        assertThatThrownBy(() -> underTest.delete(id))
                .isInstanceOf(CountryNotFoundException.class);

        //then
        verify(countryRepository, never()).delete(any());
    }

}
