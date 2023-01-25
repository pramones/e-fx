package org.pk.efx.service;

import lombok.extern.slf4j.Slf4j;
import org.pk.efx.exception.ApplicationRuntimeException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
public class PriceAdjusterService {

    public BigDecimal adjust(BigDecimal value, BigDecimal margin) {
        if (value == null) {
            throw new ApplicationRuntimeException("Value to be adjusted cannot be null");
        }
        if (margin != null) {
            BigDecimal difference = value.multiply(margin);
            log.debug("Adjusting value [{}] by difference [{}]", value, difference);
            return value.add(value.multiply(margin));
        } else {
            log.debug("Returning original value [{}]", value);
            return value;
        }

    }
}
