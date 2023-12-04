package com.example.model.dto.source.other;

import com.example.utils.annotation.AssignFrom;
import com.example.utils.annotation.CsvFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

public @Data
class GokubeOnlineLineAndOffLineInfo implements Serializable {
    private Integer code;
    private String plate;
    private Double lat;
    private Double lon;
    private Integer power;
    private String status;
    @CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date srcUpdateTime;
    @CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
    @CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @AssignFrom(name = "srcUpdateTime")
    private Date infoTime;
    @CsvFormat(pattern = "yyyy-MM-dd")
    @AssignFrom(name = "infoTime")
    private Date infoDate;

}
