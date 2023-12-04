package com.example.model.entity.mysql;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.example.model.entity.mysql.pk.IntersectionTurnDynamicMasterKey;
import com.example.model.entity.mysql.pk.IntersectionTurnDynamicTurnKey;
import com.example.utils.annotation.CsvFormat;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(catalog = "dataset", name = "intersection_turn_dynamic_turn")
@IdClass(IntersectionTurnDynamicTurnKey.class)
public @Data
class IntersectionTurnDynamicTurn implements Serializable {

    @Id
    @Column(name="inter_id")
    private String interID;
    @Id
    @Column(name="direction_start")
    private Integer directionStart;
    @Id
    @Column(name="direction_end")
    private Integer directionEnd;
    @Column(name="turn_description")
    private Integer turnDescription;
    @Column(name="total_v")
    private Integer totalV;
    private Double pcu;
    @Column(name="coach_t")
    private Integer coachT;
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
