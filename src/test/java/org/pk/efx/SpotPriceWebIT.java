package org.pk.efx;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.pk.efx.model.SpotPrice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
@DirtiesContext
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SpotPriceWebIT {

    @Value(value = "${local.server.port}")
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String SPOT_PRICE_ENDPOINT_PATTERN;

    @BeforeEach
    void beforeEach() {
        SPOT_PRICE_ENDPOINT_PATTERN = String.format("http://localhost:%s/spotPrices/", port) + "%s";
    }

    @ParameterizedTest()
    @ValueSource(longs = {106L, 109L, 110L})
    void shouldGetSpotPrice(Long value) {
        SpotPrice spotPrice = restTemplate.getForObject(
                String.format(SPOT_PRICE_ENDPOINT_PATTERN, value),
                SpotPrice.class);
        log.info("SpotPrice [{}]", spotPrice);
        assertNotNull(spotPrice.getInstrument());
        assertNotNull(spotPrice.getBid());
        assertNotNull(spotPrice.getAsk());
        assertNotNull(spotPrice.getTimestamp());
    }
}
