package com.example.model.dto.source.other;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public @Data class Scooter implements Serializable {

    private Integer code;
    private List<ScooterData> data;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm" , timezone = "GMT+8")
    private Date updatetime;
}
