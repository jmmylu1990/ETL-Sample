package com.example.model.dto.source.alert;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

public @Data class Parameter implements Serializable {

    @JsonProperty("valueName")
    private String name;
    @JsonProperty("value")
    private String value;

//    @Override
//    public String toString() {
//        StringBuilder builder = new StringBuilder();
//        builder.append(name).append("\t").append(value).append("\t");
//        return builder.toString();
//    }
}
