package com.example.model.entity.mysql;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.example.model.entity.mysql.pk.IntersectionStaticInfoDetailKey;
import com.example.utils.annotation.CsvFormat;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
@Entity
@Table(catalog = "dataset",name="intersection_static_info_detail_history")
@IdClass(IntersectionStaticInfoDetailHistoryKey.class)
public @Data class IntersectionStaticInfoDetailHistory implements Serializable {
    @Id
    @Column(name = "inter_id")
    private String interID;
    @Id
    private Integer direction;
    @Column(name = "direction_description")
    private String directionDescription;
    @Column(name = "out_link_id")
    private String outLinkID;
    @Column(name = "in_link_id")
    private String inLinkID;
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
