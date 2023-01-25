package org.pk.efx.model.feed;

import lombok.extern.slf4j.Slf4j;
import org.pk.efx.service.SpotPriceService;
import org.pk.efx.util.ApplicationUtil;
import org.springframework.stereotype.Component;

/**
 * Spot price POJO (to be integrated with provider e.g. JMS, Kafka)
 *
 * @since 1.0
 */
@Slf4j
@Component
public class SpotPriceListener {

    private SpotPriceService spotPriceService;

    public SpotPriceListener(SpotPriceService spotPriceService) {
        this.spotPriceService = spotPriceService;
    }

    public void onSpotPrice(String chunk) {
        log.debug("Processing SpotPrice chunk");
        if (ApplicationUtil.notBlank(chunk)) spotPriceService.process(chunk);
    }

}
