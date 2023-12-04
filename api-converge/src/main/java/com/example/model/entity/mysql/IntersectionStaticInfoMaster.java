package com.example.model.entity.mysql;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.example.model.entity.mysql.pk.IntersectionStaticInfoMasterKey;
import com.example.utils.annotation.CsvFormat;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(catalog = "dataset",name="intersection_static_info_master")
@IdClass(IntersectionStaticInfoMasterKey.class)
public @Data class IntersectionStaticInfoMaster implements Serializable {

    @Id
    @Column(name="inter_id")
    private String interID;
    @Column(name="inter_name")
    private String interName;
    private String source;
    private Double lon;
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
