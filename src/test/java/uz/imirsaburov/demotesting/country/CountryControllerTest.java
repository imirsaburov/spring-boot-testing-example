package uz.imirsaburov.demotesting.country;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.bind.annotation.RequestMapping;
import uz.imirsaburov.demotesting.controllers.CountryController;
import uz.imirsaburov.demotesting.entities.CountryEntity;
import uz.imirsaburov.demotesting.enums.ErrorCode;
import uz.imirsaburov.demotesting.models.RestResponsePage;
import uz.imirsaburov.demotesting.models.country.CountryDTO;
import uz.imirsaburov.demotesting.models.country.CountryFilter;
import uz.imirsaburov.demotesting.models.country.CreateCountryDTO;
import uz.imirsaburov.demotesting.models.country.UpdateCountryDTO;
import uz.imirsaburov.demotesting.models.error.DefaultErrorDTO;
import uz.imirsaburov.demotesting.repositories.CountryRepository;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CountryControllerTest {

    private static final String GLOBAL_ENDPOINT =
            CountryController.class.getAnnotation(RequestMapping.class).value()[0];


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @AfterEach
    void afterEachTest() {
        countryRepository.deleteAll();
    }


    @Test
    public void canAddCountry() throws Exception {
        //given
        final String name = "Uzbekistan";
        final CreateCountryDTO createCountryDTO = new CreateCountryDTO();
        createCountryDTO.setName(name);

        //when
        ResultActions perform = mockMvc.perform(post(GLOBAL_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createCountryDTO)));

        //then
        perform.andExpect(status().isCreated());

        assertThat(countryRepository.existsByNameContainingIgnoreCase(name)).isTrue();
    }

    @Test
    public void willThrowWhenCountryAddCountryNameExistException() throws Exception {
        //given
        final String name = "Uzbekistan";

        countryRepository.save(new CountryEntity(null, name));

        final CreateCountryDTO createCountryDTO = new CreateCountryDTO();
        createCountryDTO.setName(name);

        //when
        ResultActions perform = mockMvc.perform(post(GLOBAL_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createCountryDTO)));

        //then
        perform.andExpect(status().isBadRequest());

        String responseBodyAsString = perform.andReturn().getResponse().getContentAsString();
        DefaultErrorDTO result = objectMapper.readValue(responseBodyAsString, DefaultErrorDTO.class);
        assertThat(result.getErrorCode()).isEqualTo(ErrorCode.COUNTRY_NAME_EXIST.getCode());

        assertThat(countryRepository.existsByNameContainingIgnoreCase(name)).isTrue();
    }

    @Test
    public void canUpdateCountry() throws Exception {
        //given
        final String name = "Uzbekistan";

        CountryEntity entity = countryRepository.save(new CountryEntity(null, name));

        final UpdateCountryDTO updateCountryDTO = new UpdateCountryDTO();
        updateCountryDTO.setName(name);

        //when
        ResultActions perform = mockMvc.perform(put(GLOBAL_ENDPOINT.concat("/") + entity.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateCountryDTO)));

        //then
        perform.andExpect(status().isOk());

        assertThat(countryRepository.existsByNameContainingIgnoreCase(name)).isTrue();
    }

    @Test
    public void willThrowWhenCountryUpdateCountryNameExistException() throws Exception {
        //given
        final String name = "Uzbekistan";
        final String secondName = "USA";

        CountryEntity entity = countryRepository.save(new CountryEntity(null, name));
        CountryEntity secondEntity = countryRepository.save(new CountryEntity(null, secondName));

        final UpdateCountryDTO updateCountryDTO = new UpdateCountryDTO();
        updateCountryDTO.setName(secondName);

        //when
        ResultActions perform = mockMvc.perform(put(GLOBAL_ENDPOINT.concat("/") + entity.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateCountryDTO)));

        //then
        perform.andExpect(status().isBadRequest());

        String responseBodyAsString = perform.andReturn().getResponse().getContentAsString();
        DefaultErrorDTO result = objectMapper.readValue(responseBodyAsString, DefaultErrorDTO.class);
        assertThat(result.getErrorCode()).isEqualTo(ErrorCode.COUNTRY_NAME_EXIST.getCode());

        assertThat(countryRepository.existsByNameContainingIgnoreCaseAndIdNot(secondName, entity.getId())).isTrue();
    }

    @Test
    public void willThrowWhenCountryUpdateCountryNotFoundException() throws Exception {
        //given
        final String name = "Uzbekistan";
        final Long id = 1L;

        final UpdateCountryDTO updateCountryDTO = new UpdateCountryDTO();
        updateCountryDTO.setName(name);

        //when
        ResultActions perform = mockMvc.perform(put(GLOBAL_ENDPOINT.concat("/") + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateCountryDTO)));

        //then
        perform.andExpect(status().isNotFound());

        String responseBodyAsString = perform.andReturn().getResponse().getContentAsString();
        DefaultErrorDTO result = objectMapper.readValue(responseBodyAsString, DefaultErrorDTO.class);
        assertThat(result.getErrorCode()).isEqualTo(ErrorCode.COUNTRY_NOT_FOUND.getCode());

        assertThat(countryRepository.existsById(id)).isFalse();
    }


    @Test
    public void canGetCountryById() throws Exception {
        //given
        final String name = "Uzbekistan";
        CountryEntity entity = countryRepository.save(new CountryEntity(null, name));
        final Long id = entity.getId();

        //when
        ResultActions perform = mockMvc.perform(get(GLOBAL_ENDPOINT.concat("/") + id)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        perform.andExpect(status().isOk());

        String responseBodyAsString = perform.andReturn().getResponse().getContentAsString();
        CountryDTO result = objectMapper.readValue(responseBodyAsString, CountryDTO.class);
        assertThat(result.getName()).isEqualTo(name);
    }

    @Test
    public void willThrowGetCountryByIdCountryNotFoundException() throws Exception {
        //given
        final long id = 1L;

        //when
        ResultActions perform = mockMvc.perform(get(GLOBAL_ENDPOINT.concat("/") + id)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        perform.andExpect(status().isNotFound());
        String responseBodyAsString = perform.andReturn().getResponse().getContentAsString();
        DefaultErrorDTO result = objectMapper.readValue(responseBodyAsString, DefaultErrorDTO.class);

        assertThat(result.getErrorCode()).isEqualTo(ErrorCode.COUNTRY_NOT_FOUND.getCode());
    }

    @Test
    public void canDeleteCountryById() throws Exception {
        //given
        final String name = "Uzbekistan";
        CountryEntity entity = countryRepository.save(new CountryEntity(null, name));
        final Long id = entity.getId();

        //when
        ResultActions perform = mockMvc.perform(delete(GLOBAL_ENDPOINT.concat("/") + id)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        perform.andExpect(status().isOk());

        assertThat(countryRepository.existsById(id)).isFalse();
    }

    @Test
    public void willThrowDeleteCountryByIdCountryNotFoundException() throws Exception {
        //given
        final long id = 1L;

        //when
        ResultActions perform = mockMvc.perform(delete(GLOBAL_ENDPOINT.concat("/") + id)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        perform.andExpect(status().isNotFound());

        String responseBodyAsString = perform.andReturn().getResponse().getContentAsString();
        DefaultErrorDTO result = objectMapper.readValue(responseBodyAsString, DefaultErrorDTO.class);
        assertThat(result.getErrorCode()).isEqualTo(ErrorCode.COUNTRY_NOT_FOUND.getCode());

        assertThat(countryRepository.existsById(id)).isFalse();
    }

    @Test
    public void canGelCountriesWithoutName() throws Exception {
        //given
        final List<String> nameArray = List.of("Uzbekistan",
                "USA",
                "UK",
                "RUSSIA",
                "CANADA");

        for (String name : nameArray) {
            countryRepository.save(new CountryEntity(null, name));
        }

        CountryFilter filter = new CountryFilter();
        filter.setPage(0);
        filter.setSize(10);

        //when
        ResultActions perform = mockMvc.perform(get(GLOBAL_ENDPOINT)
                .param("page", String.valueOf(filter.getPage()))
                .param("size", String.valueOf(filter.getSize()))
                .contentType(MediaType.APPLICATION_JSON));

        //then
        perform.andExpect(status().isOk());

        String responseBodyAsString = perform.andReturn().getResponse().getContentAsString();

        TypeReference<RestResponsePage<CountryDTO>> typeReference = new TypeReference<>() {
        };
        RestResponsePage<CountryDTO> result = objectMapper.readValue(responseBodyAsString, typeReference);

        boolean resultCondition = result
                .getContent()
                .stream()
                .allMatch(countryDTO -> nameArray.contains(countryDTO.getName()));

        assertThat(resultCondition).isTrue();
    }

    @Test
    public void canGelCountriesWithName() throws Exception {
        //given
        final List<String> nameArray = List.of(
                "Uzbekistan",
                "USA",
                "UK",
                "RUSSIA",
                "CANADA");


        for (String name : nameArray) {
            countryRepository.save(new CountryEntity(null, name));
        }

        CountryFilter filter = new CountryFilter();
        filter.setPage(0);
        filter.setSize(10);
        filter.setName("U");

        final List<String> searchNameArray = nameArray
                .stream()
                .filter(n ->
                        n.toLowerCase(Locale.ROOT)
                                .contains(filter.getName()
                                        .toLowerCase(Locale.ROOT)))
                .collect(Collectors.toList());


        //when
        ResultActions perform = mockMvc.perform(get(GLOBAL_ENDPOINT)
                .param("page", String.valueOf(filter.getPage()))
                .param("size", String.valueOf(filter.getSize()))
                .param("name", String.valueOf(filter.getName()))
                .contentType(MediaType.APPLICATION_JSON));

        //then
        perform.andExpect(status().isOk());

        String responseBodyAsString = perform.andReturn().getResponse().getContentAsString();

        TypeReference<RestResponsePage<CountryDTO>> typeReference = new TypeReference<>() {
        };
        RestResponsePage<CountryDTO> result = objectMapper.readValue(responseBodyAsString, typeReference);

        boolean resultCondition = result
                .getContent()
                .stream()
                .allMatch(countryDTO -> searchNameArray.contains(countryDTO.getName()));

        assertThat(resultCondition).isTrue();
    }


}
