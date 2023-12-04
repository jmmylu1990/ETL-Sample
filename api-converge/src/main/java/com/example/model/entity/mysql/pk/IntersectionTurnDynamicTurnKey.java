package com.example.model.entity.mysql.pk;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.example.utils.annotation.CsvFormat;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

@Embeddable
public @Data class IntersectionTurnDynamicTurnKey implements Serializable {

    @Column(name="inter_id")
    private String interID;

    @Column(name="direction_start")
    private Integer directionStart;

    @Column(name="direction_end")
    private Integer directionEnd;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date SrcUpdateTime;



}
