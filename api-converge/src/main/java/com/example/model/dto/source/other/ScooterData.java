package com.example.model.dto.source.other;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

public @Data class ScooterData implements Serializable {

    @JsonProperty("Plate")
   private String plate;
    @JsonProperty("Latitude")
   private Double latitude;
    @JsonProperty("Longitude")
    private Double longitude;
    @JsonProperty("Power")
    private Integer power;
    @JsonProperty("Status")
    private String status;

}
