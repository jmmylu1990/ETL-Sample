package com.example.model.entity.mysql;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.example.model.entity.mysql.pk.RoadStaticInfoMasterHistoryKey;
import com.example.model.entity.mysql.pk.RoadTurnDynamicDirectionKey;
import com.example.utils.annotation.CsvFormat;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "dataset.road_static_info_master_history")
@IdClass(RoadStaticInfoMasterHistoryKey.class)
public @Data class RoadStaticInfoMasterHistory implements Serializable {

    @Id
    @Column(name="road_id")
    private String roadID;
    @Column(name="road_name")
    private String roadName;
    @Column(name="road_upstream")
    private String roadUpstream;
    @Column(name="road_downstream")
    private String roadDownstream;
    private String source;
    private Double lon;
    private Double lat;
    @Column(name="direction_count")
    private Integer directionCount;
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
