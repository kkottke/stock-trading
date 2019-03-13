package de.kkottke.stocktrading.common.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import de.kkottke.stocktrading.common.CustomDateSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Quote {

    private String name;
    private double price;
    @JsonSerialize(using = CustomDateSerializer.class)
    private ZonedDateTime quoteTime;
}
