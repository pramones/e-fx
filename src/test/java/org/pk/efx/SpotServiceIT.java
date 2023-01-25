package org.pk.efx;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.pk.efx.constants.ApplicationConstants;
import org.pk.efx.exception.ApplicationRuntimeException;
import org.pk.efx.model.SpotPrice;
import org.pk.efx.model.feed.SpotPriceListener;
import org.pk.efx.repository.SpotPriceRepository;
import org.pk.efx.util.ApplicationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@SpringBootTest
@DirtiesContext
public class SpotServiceIT {

    @Autowired
    SpotPriceListener spotPriceListener;

    @Autowired
    SpotPriceRepository spotPriceRepository;

    private String[] ONE_FILE = {"spot.csv"};
    private String[] MULTIPLE_FILE = {"spot1.csv", "spot2.csv", "spot3.csv", "spot4.csv"};

    private SpotPrice expectedOneFileSpotPrice = new SpotPrice();
    private SpotPrice[] expectedMultiFileSpotPrice = {new SpotPrice()};

    private LocalDateTime timestamp1 = LocalDateTime.parse("01-06-2020 12:01:01:001", ApplicationConstants.DATE_TIME_FORMATTER);
    private LocalDateTime timestamp4 = LocalDateTime.parse("01-06-2020 12:01:02:100", ApplicationConstants.DATE_TIME_FORMATTER);
    private LocalDateTime timestamp5 = LocalDateTime.parse("01-06-2020 12:01:02:110", ApplicationConstants.DATE_TIME_FORMATTER);

    SpotPrice[] expectedSpotPrices = {
            new SpotPrice(
                    106L,
                    "EUR/USD",
                    new BigDecimal("1.09890000"),
                    new BigDecimal("1.20120000"),
                    timestamp1),
            new SpotPrice(
                    109L,
                    "GBP/USD",
                    new BigDecimal("1.24865010"),
                    new BigDecimal("1.25735610"),
                    timestamp4),
            new SpotPrice(
                    110L,
                    "EUR/JPY",
                    new BigDecimal("119.49039000"),
                    new BigDecimal("120.02991000"),
                    timestamp5)
    };

    @AfterEach
    void afterEach() {
        spotPriceRepository.deleteAll();
        spotPriceRepository.flush();
    }

    @Test
    void shouldProcessOneFile() {
        load(ONE_FILE);

        List<SpotPrice> spotPrices = spotPriceRepository.findAll(Sort.by(Sort.Order.asc("id")));

        spotPrices.stream().forEach(System.out::println);
        assertEquals(3, spotPrices.size(), "More than 3 SpotPrice found in the database");

        Arrays.stream(expectedSpotPrices).forEach(System.out::println);
        Set<SpotPrice> expectedSpotPriceSet = new HashSet<>(Arrays.asList(expectedSpotPrices));
        assertTrue(expectedSpotPriceSet.containsAll(spotPrices), "SpotPrice in the database differs from expected one");
    }

    @Test
    void shouldProcessMultipleFile() {
        load(MULTIPLE_FILE);

        List<SpotPrice> spotPrices = spotPriceRepository.findAll(Sort.by(Sort.Order.asc("id")));
        assertEquals(3, spotPrices.size(), "Number of currency pairs in the database more that 3 expected");
    }

    private void load(String[] files) {
        Arrays.stream(files).forEach((file) -> {
            Path path = Paths.get(ApplicationUtil.getRootPath(), ApplicationConstants.FEED_FOLDER, file);
            try (Stream<String> lines = Files.lines(path)) {
                lines.forEach(spotPriceListener::onSpotPrice);
            } catch (IOException e) {
                throw new ApplicationRuntimeException(e);
            }
        });
    }
}
