package org.pk.efx.constants;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

public class ApplicationConstants {

    public static final String NEW_LINE_REGEX = "\\R";
    public static final String CSV_COLUMN_DELIMITER = ",";

    public static final String FEED_FOLDER = "feed";
    public static final String FEED_CSV_FILE = "spot.csv";

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss:SSS");

    public static final BigDecimal ASK_MARGIN = new BigDecimal("0.001");
    public static final BigDecimal BID_MARGIN = new BigDecimal("-0.001");


}
