package org.pk.efx.service;

import lombok.extern.slf4j.Slf4j;
import org.pk.efx.constants.ApplicationConstants;
import org.pk.efx.model.SpotPrice;
import org.pk.efx.repository.SpotPriceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class SpotPriceService {

    private SpotPriceRepository spotPriceRepository;
    private PriceAdjusterService priceAdjusterService;

    public SpotPriceService(SpotPriceRepository spotPriceRepository, PriceAdjusterService priceAdjusterService) {
        this.spotPriceRepository = spotPriceRepository;
        this.priceAdjusterService = priceAdjusterService;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void process(String chunk) {
        update(parse(chunk));
    }

    public List<SpotPrice> parse(String chunk) {
        log.debug("Processing SpotPrice's feed chunk");
        List<SpotPrice> spotPrices = Stream.of(chunk.split(ApplicationConstants.NEW_LINE_REGEX))
                .filter((line) -> line != null && !line.isBlank())
                .map((line) -> {
                    String[] parts = line.split(ApplicationConstants.CSV_COLUMN_DELIMITER);
                    log.debug("Converting line [{}] -> parts [{}]", line, parts);
                    SpotPrice spotPrice = SpotPrice.builder()
                            .id(Long.valueOf(parts[0].trim()))
                            .instrument(parts[1].trim())
                            .bid(priceAdjusterService
                                    .adjust(new BigDecimal(parts[2].trim()), ApplicationConstants.BID_MARGIN))
                            .ask(priceAdjusterService
                                    .adjust(new BigDecimal(parts[3].trim()), ApplicationConstants.ASK_MARGIN))
                            .timestamp(
                                    LocalDateTime.parse(parts[4].trim(), ApplicationConstants.DATE_TIME_FORMATTER)
                            )
                            .build();

                    return spotPrice;
                }).collect(Collectors.toList());
        return spotPrices;
    }

    public void update(List<SpotPrice> spotPrices) {
        log.debug("Updating SpotPrice's in the database");
        spotPrices.forEach((spotPrice) -> {
            spotPriceRepository.deleteByInstrumentAndTimestampBefore(spotPrice.getInstrument(), spotPrice.getTimestamp());
            spotPriceRepository.save(spotPrice);
        });
        log.debug("SpotPrice's has been updated in the database");
    }
}
