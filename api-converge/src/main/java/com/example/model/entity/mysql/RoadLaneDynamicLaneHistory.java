package com.example.model.entity.mysql;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.example.model.entity.mysql.pk.RoadLaneDynamicDirectionHistoryKey;
import com.example.model.entity.mysql.pk.RoadLaneDynamicLaneHistoryKey;
import com.example.utils.annotation.CsvFormat;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "dataset.road_lane_dynamic_lane_history")
@IdClass(RoadLaneDynamicLaneHistoryKey.class)
public @Data class RoadLaneDynamicLaneHistory implements Serializable {


    @Id
    @Column(name="road_id")
    private String roadID;
    @Id
    private Integer direction;
    @Id
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
    private Integer laneoccupy;
    @Id
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date srcUpdateTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date infoTime;
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @CsvFormat(pattern = "yyyy-MM-dd")
    private Date infoDate;

}
