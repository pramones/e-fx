package org.pk.efx.boot;

import lombok.extern.slf4j.Slf4j;
import org.pk.efx.constants.ApplicationConstants;
import org.pk.efx.model.feed.SpotPriceListener;
import org.pk.efx.util.ApplicationUtil;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

@Slf4j
@Configuration
public class FeedRunner {

    SpotPriceListener spotPriceListener;

    public FeedRunner(SpotPriceListener spotPriceListener) {
        this.spotPriceListener = spotPriceListener;
    }

    @Bean
    public CommandLineRunner loadFeed() {
        return args -> {
            Path path = Paths.get(
                    ApplicationUtil.getRootPath(),
                    ApplicationConstants.FEED_FOLDER,
                    ApplicationConstants.FEED_CSV_FILE
            );
            log.info("Loading SpotPrice feed from [{}]", path);
            try (Stream<String> lines = Files.lines(path)) {
                lines.forEach(spotPriceListener::onSpotPrice);
            }
        };
    }
}
