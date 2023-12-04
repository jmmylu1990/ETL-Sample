package com.example.model.entity.mysql;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.example.model.entity.mysql.pk.IntersectionEventDynamicHistoryKey;
import com.example.utils.annotation.CsvFormat;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(catalog = "dataset",name = "intersection_event_dynamic_history")
@IdClass(IntersectionEventDynamicHistoryKey.class)
public @Data class IntersectionEventDynamicHistory implements Serializable {
    @Id
    @Column(name="inter_id")
    private String interId;
    @Column(name="data_datatime")
    private Date dataDatatime;
    @Column(name="event_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date eventTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name="event_end_dt")
    private Date eventEndDt;
    private String direction;
    @Id
    @Column(name="object_id")
    private String objectId;
    @Column(name="event_type")
    private Integer eventType;
    @Column(name="vehicle_type")
    private Integer vehicleType;
    @Column(name="plate_no")
    private String plateNo;
    @Column(name="start_photo")
    private String startPhoto;
    @Column(name="start_photo_id")
    private String startPhotoId;
    @Column(name="end_photo")
    private String endPhoto;
    @Column(name="end_photo_id")
    private String endPhotoId;
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
