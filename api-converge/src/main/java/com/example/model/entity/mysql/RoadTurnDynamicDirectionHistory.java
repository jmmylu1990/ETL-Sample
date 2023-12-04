package com.example.model.entity.mysql;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.example.model.entity.mysql.pk.RoadTurnDynamicDirectionHistoryKey;
import com.example.model.entity.mysql.pk.RoadTurnDynamicMasterHistoryKey;
import com.example.utils.annotation.CsvFormat;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(catalog = "dataset",name = "road_turn_dynamic_direction_history")
@IdClass(RoadTurnDynamicDirectionHistoryKey.class)
public @Data class RoadTurnDynamicDirectionHistory implements Serializable {
    @Id
    @Column(name="road_id")
    private String roadID;
    @Id
    private Integer direction;
    @Column(name="total_v")
    private Integer totalV;
    private Integer pcu;
    private Integer coach;
    private Integer motortruck;
    private Integer motorbus;
    private Integer tourbus;
    private Integer microbus;
    private Integer tanktruck;
    private Integer crane;
    private Integer fireengine;
    private Integer garbagetruck;
    private Integer stacker;
    private Integer concretemixertruck;
    private Integer tractor;
    private Integer fulltrailer;
    private Integer semitrailer;
    private Integer dump;
    @Column(name="car_t")
    private Integer carT;
    @Column(name="pickup_truck")
    private Integer pickupTruck;
    private Integer car;
    private Integer taxi;
    private Integer ambulance;
    private Integer motorcycle;
    private Integer bicycle;
    private Integer pedestrian;
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
