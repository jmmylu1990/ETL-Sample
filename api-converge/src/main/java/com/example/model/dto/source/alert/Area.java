package com.example.model.dto.source.alert;

import lombok.Data;

import java.io.Serializable;

public @Data class Area implements Serializable {
    private String areaDesc;
    private Geocode geocode;

}
