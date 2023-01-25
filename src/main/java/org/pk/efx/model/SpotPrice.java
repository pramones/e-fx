package org.pk.efx.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table
@EqualsAndHashCode
public class SpotPrice {

    @Id
    Long id;

    String instrument;

    @Column(precision = 24, scale = 8)
    BigDecimal bid;

    @Column(precision = 24, scale = 8)
    BigDecimal ask;

    LocalDateTime timestamp;

}
