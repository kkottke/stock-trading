package de.kkottke.stocktrading.common.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class Quote {

    private String exchange;
    private String name;
    private double price;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private ZonedDateTime quoteTime;
}
