package com.example.model.entity.mysql;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.example.model.entity.mysql.pk.IntersectionTurnDynamicDirectionKey;
import com.example.model.entity.mysql.pk.IntersectionTurnDynamicMasterKey;
import com.example.utils.annotation.CsvFormat;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(catalog = "dataset",name="intersection_turn_dynamic_direction")
@IdClass(IntersectionTurnDynamicDirectionKey.class)
public @Data class IntersectionTurnDynamicDirection implements Serializable {


    @Id
    @Column(name="inter_id")
    private String interID;
    @Id
    @Column(name="direction_start")
    private Integer directionStart;
    @Column(name="dir_v")
    private Integer dirV;
    @Column(name="dir_p")
    private Double dirP;
    @Column(name="turn_count")
    private Double turnCount;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name="direction_count")
    private Integer directionCount;
    @Id
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date SrcUpdateTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date UpdateTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date InfoTime;
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @CsvFormat(pattern = "yyyy-MM-dd")
    private Date InfoDate;


}
