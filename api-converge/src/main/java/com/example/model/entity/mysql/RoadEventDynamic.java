package com.example.model.entity.mysql;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.example.model.entity.mysql.pk.IntersectionEventDynamicKey;
import com.example.model.entity.mysql.pk.RoadEventDynamicKey;
import com.example.utils.annotation.CsvFormat;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(catalog = "dataset",name = "road_event_dynamic")
@IdClass(RoadEventDynamicKey.class)
public @Data class RoadEventDynamic implements Serializable {

    @Id
    @Column(name="road_id")
    private String roadID;
    @Column(name="data_datatime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date dataDatatime;
    @Column(name="event_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date  eventTime;
    @Column(name="event_start_dt")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date eventStartDt;
    @Column(name="event_end_dt")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date eventEndDt;
    private String direction;
    @Id
    @Column(name="object_id")
    private String objectID;
    @Column(name="event_type")
    private Integer eventType;
    @Column(name="vehicle_type")
    private Integer vehicleType;
    @Column(name="plate_no")
    private String plateNo;
    @Column(name="start_photo")
    private String startPhoto;
    @Column(name="start_photo_id")
    private String startPhotoDd;
    @Column(name="end_photo")
    private String endPhoto;
    @Column(name="end_photo_id")
    private String endPhotoID;
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
