package com.example.model.entity.mysql.pk;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.example.utils.annotation.CsvFormat;
import lombok.Data;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Date;

@Embeddable
public @Data class IntersectionTurnDynamicMasterKey implements Serializable {


    private String interID;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date SrcUpdateTime;


}
