package com.example.model.dto.source.other;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.example.utils.annotation.AssignFrom;
import com.example.utils.annotation.CsvFormat;
import lombok.Data;

import javax.persistence.Column;
import java.io.Serializable;
import java.util.Date;

public @Data
class RoadExcavationMinGuo implements Serializable {

    private String sysID;
    @JsonProperty("FROM_NO")
    private String fromNO;
    private String company;
    private String phone;
    @JsonProperty("PMT_NO")
    private String pmtNO;
    private String area;
    private String location;

    @JsonProperty("RMT_DATE")
    @JsonFormat(pattern = "yyyMMdd")
    @CsvFormat(pattern = "yyyy-MM-dd")
    private Date rmtDate;
    @JsonProperty("DATE_DIG_S")
    @JsonFormat(pattern = "yyyMMdd")
    @CsvFormat(pattern = "yyyy-MM-dd")
    private Date dateDigS;
    @JsonProperty("DATE_DIG_E")
    @JsonFormat(pattern = "yyyMMdd")
    @CsvFormat(pattern = "yyyy-MM-dd")
    private Date dateDigE;
    @JsonProperty("DATE_EXT_S")
    @JsonFormat(pattern = "yyyMMdd")
    @CsvFormat(pattern = "yyyy-MM-dd")
    private Date dateExtS;
    @JsonProperty("DATE_EXT_E")
    @JsonFormat(pattern = "yyyMMdd")
    @CsvFormat(pattern = "yyyy-MM-dd")
    private Date dateExtE;
    @JsonProperty("APPLY_TO")
    private String applyTo;
    private String reason;
    private String result;
    private Double lat;
    private Double lng;
    @CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date srcUpdateTime;
    @CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
    @CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @AssignFrom(name = "srcUpdateTime")
    private Date infoTime;
    @CsvFormat(pattern = "yyyy-MM-dd")
    @AssignFrom(name = "srcUpdateTime")
    private Date infoDate;
}
