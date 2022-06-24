package uz.imirsaburov.demotesting.country;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import uz.imirsaburov.demotesting.entities.CountryEntity;
import uz.imirsaburov.demotesting.repositories.CountryRepository;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class CountryRepositoryTest {

    @Autowired
    private CountryRepository underTest;

    @AfterEach
    void afterEachTest() {
        underTest.deleteAll();
    }


    @Test
    void itShouldExistCountryName() {
        //given
        final String name = "Uzbekistan";
        CountryEntity countryEntity = new CountryEntity();
        countryEntity.setName(name);
        underTest.save(countryEntity);

        //when
        boolean result = underTest.existsByNameContainingIgnoreCase(name);

        //then
        assertThat(result).isEqualTo(true);
    }

    @Test
    void itShouldExistCountryNameAndIdNot() {
        //given
        final String name = "Uzbekistan";

        CountryEntity countryEntity = new CountryEntity();
        countryEntity.setName(name);
        underTest.save(countryEntity);

        CountryEntity countryEntitySecond = new CountryEntity();
        countryEntitySecond.setName(name.concat("2"));
        underTest.save(countryEntitySecond);

        //when
        boolean result = underTest.existsByNameContainingIgnoreCaseAndIdNot(name, countryEntitySecond.getId());

//        then
        assertThat(result).isEqualTo(true);
    }

    @Test
    void itShouldGetCountryPage() {
        //given
        String searchName = "uzb";
        PageRequest pageRequest = PageRequest.of(0, 10);

        final String[] countryNameArray = {
                "Uzbekistan",
                "USA",
                "UK"
        };

        for (String country : countryNameArray) {
            CountryEntity countryEntity = new CountryEntity();
            countryEntity.setName(country);
            underTest.save(countryEntity);
        }

        //when
        Page<CountryEntity> result = underTest.findAllByNameContainingIgnoreCase(searchName, pageRequest);

        boolean resultCondition = result.getContent().stream().allMatch(e -> e.getName().toLowerCase(Locale.ROOT).contains(searchName.toLowerCase(Locale.ROOT)));

        //then
        assertThat(resultCondition).isEqualTo(true);
        assertThat(result.getSize()).isEqualTo(pageRequest.getPageSize());
        assertThat(result.getNumber()).isEqualTo(pageRequest.getPageNumber());
    }
}
