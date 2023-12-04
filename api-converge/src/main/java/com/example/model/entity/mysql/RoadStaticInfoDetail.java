package com.example.model.entity.mysql;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.example.model.entity.mysql.pk.RoadStaticInfoDetailKey;
import com.example.model.entity.mysql.pk.RoadStaticInfoMasterKey;
import com.example.utils.annotation.CsvFormat;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(catalog = "dataset", name = "road_static_info_detail")
@IdClass(RoadStaticInfoDetailKey.class)
public @Data
class RoadStaticInfoDetail implements Serializable {

    @Id
    @Column(name = "road_id")
    private String roadID;
    @Id
    private Integer direction;
    @Column(name = "road_description")
    private String roadDescription;
    @Column(name = "link_id")
    private String linkID;
    @Column(name = "lane_count")
    private Integer laneCount;
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
