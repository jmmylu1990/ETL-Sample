package com.example.model.entity.mysql;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.example.model.entity.mysql.pk.RoadStaticInfoDetailHistoryKey;
import com.example.model.entity.mysql.pk.RoadStaticInfoMasterHistoryKey;
import com.example.utils.annotation.CsvFormat;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "dataset.road_static_info_detail_history")
@IdClass(RoadStaticInfoDetailHistoryKey.class)
public @Data
class RoadStaticInfoDetailHistory implements Serializable {

    @Id
    @Column(name = "road_id")
    private String roadID;
    @Id
    private Integer direction;
    @Column(name = "road_description")
    private String roadDescription;
    @Column(name = "road_type")
    private String roadType;
    @Column(name = "link_id")
    private String linkID;
    @Column(name = "lane_count")
    private Integer laneCount;
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
