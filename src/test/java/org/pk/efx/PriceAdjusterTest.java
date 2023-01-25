package org.pk.efx;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pk.efx.exception.ApplicationRuntimeException;
import org.pk.efx.service.PriceAdjusterService;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PriceAdjusterTest {

    private PriceAdjusterService priceAdjusterService;

    private final BigDecimal[][] POSITIVE_AMOUNT_AND_EXPECTED_MARGIN_PLUS_1_PERCENT = {
            {new BigDecimal("1.0"), new BigDecimal("1.01")},
            {new BigDecimal("10.0"), new BigDecimal("10.1")},
            {new BigDecimal("100.0"), new BigDecimal("101.0")}
    };

    private final BigDecimal[][] POSITIVE_AMOUNT_AND_EXPECTED_MARGIN_MINUS_1_PERCENT = {
            {new BigDecimal("1.0"), new BigDecimal("0.99")},
            {new BigDecimal("10.0"), new BigDecimal("9.9")},
            {new BigDecimal("100.0"), new BigDecimal("99.0")}
    };

    private final BigDecimal PLUS_ONE_PERCENT = new BigDecimal("0.01");
    private final BigDecimal MINUS_ONE_PERCENT = new BigDecimal("-0.01");

    @BeforeEach
    void beforeAll() {
        priceAdjusterService = new PriceAdjusterService();
    }

    @Test
    void shouldAdjustByPositiveMargin() {
        testAdjust(POSITIVE_AMOUNT_AND_EXPECTED_MARGIN_PLUS_1_PERCENT, PLUS_ONE_PERCENT);
    }

    @Test
    void shouldAdjustByNegativeMargin() {
        testAdjust(POSITIVE_AMOUNT_AND_EXPECTED_MARGIN_MINUS_1_PERCENT, MINUS_ONE_PERCENT);
    }

    private void testAdjust(BigDecimal[][] sourceAndExpected, BigDecimal margin) {
        Arrays.stream(sourceAndExpected)
                .forEach((item) -> {
                    BigDecimal adjusted = priceAdjusterService.adjust(item[0], margin);
                    assertEquals(item[1].compareTo(adjusted), 0);
                });
    }

    @Test
    void shouldThrowApplicationException() {
        assertThrows(ApplicationRuntimeException.class, () -> {
            priceAdjusterService.adjust(null, PLUS_ONE_PERCENT);
        });
    }

    @Test
    void shouldReturnOriginalValue() {
        BigDecimal result = priceAdjusterService.adjust(PLUS_ONE_PERCENT, null);
        assertEquals(PLUS_ONE_PERCENT.compareTo(result), 0);
    }
}
