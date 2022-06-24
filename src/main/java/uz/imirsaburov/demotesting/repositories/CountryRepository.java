package uz.imirsaburov.demotesting.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.imirsaburov.demotesting.entities.CountryEntity;

@Repository(value = "country-repository")
public interface CountryRepository extends JpaRepository<CountryEntity, Long> {

    boolean existsByNameContainingIgnoreCase(String name);
    boolean existsByNameContainingIgnoreCaseAndIdNot(String name, Long id);

    Page<CountryEntity> findAllByNameContainingIgnoreCase(String name, Pageable pageable);
}
