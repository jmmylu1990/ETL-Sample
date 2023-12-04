package com.example.model.dto.source.alert;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.example.utils.annotation.CsvFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public @Data class AlertDumpDatastoreInfoList implements Serializable {

    private String identifier;
    private String sender;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "GMT+8")
    @CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date sent;
    private String status;
    private String msgType;
    private String source;
    private String scope;
    private String references;
    @JsonProperty("info")
    private List<Info> infos;
}
