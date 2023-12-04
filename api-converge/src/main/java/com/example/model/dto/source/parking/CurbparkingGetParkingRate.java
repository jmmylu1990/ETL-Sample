package com.example.model.dto.source.parking;

import com.example.utils.annotation.AssignFrom;
import com.example.utils.annotation.CsvFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

public @Data class CurbparkingGetParkingRate implements Serializable {

    private String roadCode;
    private String road;
    private Integer fullParkingSpace;
    private Integer parkingSpace;
    @CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date srcUpdateTime;
    @CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
    @AssignFrom(name = "srcUpdateTime")
    @CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date infoTime;
    @AssignFrom(name = "infoTime")
    @CsvFormat(pattern = "yyyy-MM-dd")
    private Date infoDate;

}
