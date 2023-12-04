package com.example.model.entity.mysql;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.example.model.entity.mysql.pk.IntersectionStaticInfoDetailKey;
import com.example.utils.annotation.CsvFormat;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(catalog = "dataset",name="intersection_static_info_detail")
@IdClass(IntersectionStaticInfoDetailKey.class)
public @Data class IntersectionStaticInfoDetail implements Serializable {

    @Id
    @Column(name="inter_id")
    private String  interID;
    private Integer direction;
    @Column(name="direction_description")
    private String direction_description;
    @Column(name="out_link_id")
    private String  outLinkID;
    @Column(name="in_link_id")
    private String  inLinkID;
    @Column(name="lane_count")
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
