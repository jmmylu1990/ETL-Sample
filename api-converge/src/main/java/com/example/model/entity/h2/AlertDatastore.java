package com.example.model.entity.h2;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "alert_datastore")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.UpperCamelCaseStrategy.class)
public @Data class AlertDatastore implements Serializable {

    @Id
    @Column(name = "cap_id")
    private String capId;
    @Column(name = "cap_code")
    private String capCode;
    @Column(name = "gov_code")
    private String govCode;
    @Column(name = "county_code ")
    private String countyCode;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "GMT+8")
    private String effective;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "GMT+8")
    private Date expires;
    private String description;
}
