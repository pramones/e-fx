package org.pk.efx;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pk.efx.model.feed.SpotPriceListener;
import org.pk.efx.service.SpotPriceService;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SpotPriceListenerTest {

    @InjectMocks
    private SpotPriceListener spotPriceListener;

    @Mock
    private SpotPriceService spotPriceServiceMock;

    private String ONE_LINE_SPOT_PRICE = "106, EUR/USD, 1.1000,1.2000,01-06-2020 12:01:01:001";
    private String MULTI_LINE_SPOT_PRICES = """
                    106, EUR/USD, 1.1000,1.2000,01-06-2020 12:01:01:001
                    107, EUR/JPY, 119.60,119.90,01-06-2020 12:01:02:002
                    108, GBP/USD, 1.2500,1.2560,01-06-2020 12:01:02:002
                    109, GBP/USD, 1.2499,1.2561,01-06-2020 12:01:02:100
                    110, EUR/JPY, 119.61,119.91,01-06-2020 12:01:02:110
            """;


    @BeforeEach
    void beforeEach() {
    }

    @Test
    void shouldAcceptOneChunk() {
        spotPriceListener.onSpotPrice(ONE_LINE_SPOT_PRICE);
        verify(spotPriceServiceMock, times(1)).process(ONE_LINE_SPOT_PRICE);
        spotPriceListener.onSpotPrice(MULTI_LINE_SPOT_PRICES);
        verify(spotPriceServiceMock, times(1)).process(MULTI_LINE_SPOT_PRICES);
    }

    @Test
    void shouldSkipEmptyChunk() {
        spotPriceListener.onSpotPrice("");
        spotPriceListener.onSpotPrice("   ");
        verify(spotPriceServiceMock, never()).process(anyString());
    }
}
