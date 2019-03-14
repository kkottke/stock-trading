package de.kkottke.stocktrading.common.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import de.kkottke.stocktrading.common.CustomDateDeserializer;
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
    private String symbol;
    private double price;
    @JsonSerialize(using = CustomDateSerializer.class)
    @JsonDeserialize(using = CustomDateDeserializer.class)
    private ZonedDateTime quoteTime;
}
