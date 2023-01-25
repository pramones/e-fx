package org.pk.efx;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pk.efx.constants.ApplicationConstants;
import org.pk.efx.model.SpotPrice;
import org.pk.efx.repository.SpotPriceRepository;
import org.pk.efx.service.PriceAdjusterService;
import org.pk.efx.service.SpotPriceService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SpotPriceServiceTest {

    @Mock
    private SpotPriceRepository spotPriceRepository;

    @Mock
    private PriceAdjusterService priceAdjusterService;

    @InjectMocks
    private SpotPriceService spotPriceService;

    private String ONE_LINE_SPOT_PRICE = "106, EUR/USD, 1.1000,1.2000,01-06-2020 12:01:01:001";
    private String MULTI_LINE_SPOT_PRICES = """
                            106, EUR/USD, 1.1,1.2,01-06-2020 12:01:01:001
                            107, EUR/JPY, 119,120,01-06-2020 12:01:02:002
                            108, GBP/USD, 1.25,1.26,01-06-2020 12:01:02:002
                            109, GBP/USD, 1.24,1.25,01-06-2020 12:01:02:100
                            110, EUR/JPY, 119.5,120.5,01-06-2020 12:01:02:110
            """;

    private LocalDateTime timestamp1 = LocalDateTime.parse("01-06-2020 12:01:01:001", ApplicationConstants.DATE_TIME_FORMATTER);
    private LocalDateTime timestamp2 = LocalDateTime.parse("01-06-2020 12:01:02:002", ApplicationConstants.DATE_TIME_FORMATTER);
    private LocalDateTime timestamp3 = LocalDateTime.parse("01-06-2020 12:01:02:002", ApplicationConstants.DATE_TIME_FORMATTER);
    private LocalDateTime timestamp4 = LocalDateTime.parse("01-06-2020 12:01:02:100", ApplicationConstants.DATE_TIME_FORMATTER);
    private LocalDateTime timestamp5 = LocalDateTime.parse("01-06-2020 12:01:02:110", ApplicationConstants.DATE_TIME_FORMATTER);

    SpotPrice[] expectedSpotPrices = {
            new SpotPrice(
                    106L,
                    "EUR/USD",
                    BigDecimal.ONE,
                    BigDecimal.ONE,
                    timestamp1),
            new SpotPrice(
                    107L,
                    "EUR/JPY",
                    BigDecimal.ONE,
                    BigDecimal.ONE,
                    timestamp2),
            new SpotPrice(
                    108L,
                    "GBP/USD",
                    BigDecimal.ONE,
                    BigDecimal.ONE,
                    timestamp3),
            new SpotPrice(
                    109L,
                    "GBP/USD",
                    BigDecimal.ONE,
                    BigDecimal.ONE,
                    timestamp4),
            new SpotPrice(
                    110L,
                    "EUR/JPY",
                    BigDecimal.ONE,
                    BigDecimal.ONE,
                    timestamp5)
    };


    @BeforeEach
    void beforeEach() {
        when(priceAdjusterService.adjust(any(), any())).thenReturn(BigDecimal.ONE);
    }

    @Test
    void shouldProcessOneLine() {
        spotPriceService.process(ONE_LINE_SPOT_PRICE);

        LocalDateTime timestamp = LocalDateTime.parse("01-06-2020 12:01:01:001", ApplicationConstants.DATE_TIME_FORMATTER);
        SpotPrice expectedSpotPrice = new SpotPrice(
                106L,
                "EUR/USD",
                priceAdjusterService.adjust(new BigDecimal("1.1000"), ApplicationConstants.BID_MARGIN),
                priceAdjusterService.adjust(new BigDecimal("1.2000"), ApplicationConstants.ASK_MARGIN),
                timestamp);
        verify(spotPriceRepository, times(1)).deleteByInstrumentAndTimestampBefore("EUR/USD", timestamp);
        verify(spotPriceRepository, times(1)).save(expectedSpotPrice);

        verify(priceAdjusterService, times(4)).adjust(any(), any());
    }

    @Test
    void shouldProcessMultiLine() {
        spotPriceService.process(MULTI_LINE_SPOT_PRICES);

        Arrays.stream(expectedSpotPrices).forEach((spotPrice) -> {
                    verify(spotPriceRepository, times(1))
                            .deleteByInstrumentAndTimestampBefore(spotPrice.getInstrument(), spotPrice.getTimestamp());
                    verify(spotPriceRepository, times(1)).save(spotPrice);
                }
        );

        verify(priceAdjusterService, times(10)).adjust(any(), any());
    }

}
