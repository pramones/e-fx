package org.pk.efx.repository;

import org.pk.efx.model.SpotPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
@RepositoryRestResource
public interface SpotPriceRepository extends JpaRepository<SpotPrice, Long> {

    void deleteByInstrumentAndTimestampBefore(String instrument, LocalDateTime timestamp);
}
