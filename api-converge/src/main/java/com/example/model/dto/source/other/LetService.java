package com.example.model.dto.source.other;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.example.utils.annotation.AssignFrom;
import com.example.utils.annotation.CsvFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
@JsonNaming(PropertyNamingStrategy.UpperCamelCaseStrategy.class)
public @Data class LetService implements Serializable {


    private String irSID;
    private String plateNumber;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date sTime;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date eTime;
    private String beenVerified;
    private String techLawTypeSID;
    private String techLawSID;
    private String techLawName;
    private String proofImageUID;
    private String proofVideoUID;
    private String updateUserSID;
    @JsonProperty("VerifiedUser")
    private String verifiedUserSID;
    @JsonProperty("Month")
    private String dataMonth;
    @CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private java.util.Date srcUpdateTime;
    @CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
    @AssignFrom(name = "srcUpdateTime")
    @CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date infoTime;
    @AssignFrom(name = "infoTime")
    @CsvFormat(pattern = "yyyy-MM-dd")
    private Date infoDate;
}
