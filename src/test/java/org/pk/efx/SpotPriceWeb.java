package org.pk.efx;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@DirtiesContext
@SpringBootTest
public class SpotPriceWeb {

    private MockMvc mockMvc;

    private String SPOT_PRICE_ENDPOINT_PATTERN;

    @BeforeEach
    void beforeEach(WebApplicationContext wac) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @AfterEach
    public void afterEach() {
        mockMvc = null;
    }

    @Test
    public void shouldGetSpotPrice() throws Exception {
        mockMvc.perform(get("/spotPrices/{id}", 106).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.instrument").value("EUR/USD"));
    }
}
