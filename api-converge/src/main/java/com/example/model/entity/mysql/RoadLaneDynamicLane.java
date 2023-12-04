package com.example.model.entity.mysql;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.example.model.entity.mysql.pk.RoadLaneDynamicDirectionKey;
import com.example.model.entity.mysql.pk.RoadLaneDynamicLaneKey;
import com.example.utils.annotation.CsvFormat;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(catalog = "dataset", name = "road_lane_dynamic_lane")
@IdClass(RoadLaneDynamicLaneKey.class)
public @Data class RoadLaneDynamicLane implements Serializable {

    @Id
    @Column(name="road_id")
    private String roadID;
    private Integer direction;
    @Column(name="lane_id")
    private Integer laneID;
    @Column(name="ql_v")
    private Integer qlV;
    @Column(name="ql_lc")
    private Integer qlLc;
    @Column(name="ql_c")
    private Integer qlC;
    @Column(name="ql_m")
    private Integer qlM;
    private Double laneoccupy;
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
