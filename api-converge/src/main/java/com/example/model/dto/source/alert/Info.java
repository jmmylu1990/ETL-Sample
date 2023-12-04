package com.example.model.dto.source.alert;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.JsonNode;
import com.example.utils.annotation.CsvFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public @Data class Info implements Serializable {
    private String language;
    private String category;
    private String event;
    private String responseType;
    private String urgency;
    private String severity;
    private String certainty;
    private EventCode eventCode;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "GMT+8")
    private Date effective;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "GMT+8")
    private Date onset;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "GMT+8")
    private Date expires;
    private String senderName;
    private String headline;
    private String description;
    private String instruction;
    private String web;
    private List<Parameter> parameters;
    private List<Area> area;
    @JsonProperty("parameter")
    public void setParameters(List<Parameter> parameters) {
        this.parameters = parameters;

    }

}
