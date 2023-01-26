package org.pk.efx.boot;

import lombok.extern.slf4j.Slf4j;
import org.pk.efx.constants.ApplicationConstants;
import org.pk.efx.model.feed.SpotPriceListener;
import org.pk.efx.util.ApplicationUtil;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class FeedRunner {

    private SpotPriceListener spotPriceListener;

    public FeedRunner(SpotPriceListener spotPriceListener) {
        this.spotPriceListener = spotPriceListener;
    }

    @Bean
    public CommandLineRunner loadFeed() {
        return args -> {
            String resource = String.format("%s/%s",
                    ApplicationConstants.FEED_FOLDER,
                    ApplicationConstants.FEED_CSV_FILE
            );
            log.info("Loading SpotPrice feed from [{}] ", resource);

            ApplicationUtil.readLines(resource)
                    .forEach(spotPriceListener::onSpotPrice);
        };
    }

}
