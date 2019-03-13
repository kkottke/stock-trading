package de.kkottke.stocktrading.common;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public interface CustomDeSer {

    DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT.withZone(ZoneOffset.UTC);
}
