package com.example.model.dto.source.alert;

import lombok.Data;

import java.io.Serializable;

public @Data class Geocode implements Serializable {

    private String valueName;
    private String value;
}
