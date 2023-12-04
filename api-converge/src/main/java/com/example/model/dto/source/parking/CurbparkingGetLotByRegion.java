package com.example.model.dto.source.parking;

import com.example.utils.annotation.AssignFrom;
import com.example.utils.annotation.CsvFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

public @Data class CurbparkingGetLotByRegion implements Serializable {

    private String id;
    private String ownerName;
    private String name;
    private Integer volumn;
    private String leftSpace;
    private Integer smallCarParkingSpace;
    private Integer  smallCarVacancy;
    private Integer largecarParkingSpace;
    private Integer largecarVacancy;
    private Integer motorcycleParkingSpace;
    private Integer  motorcycleVacancy;
    private String areaID;
    private String areaName;
    private Double lat;
    private Double lng;
    private String location;
    private String telephone;
    private String businessHours;
    private String chargeWay;
    private String specialCharging;
    private Integer smallCarcharg;
    private Integer largeCarCharg;
    private Integer motorcycleCharg;
    private Integer  smallCarChargeFrequency;
    private Integer  largeCarChargeFrequency;
    private Integer motorcycleChargeFrequency;
    private Integer charg;
    private Integer chargeFrequency;
    private String parkingLotType;
    private Integer volumnTruck;
    private Integer  volumnAuto;
    private Integer  volumnScooter;
    private Integer  volumnBike;
    private Integer  volumnDisabledCar;
    private Integer volumnDisabledMoto;
    private String memo;
    private String cate;
    @CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date srcUpdateTime;
    @CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
    @AssignFrom(name = "srcUpdateTime")
    @CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date infoTime;
    @AssignFrom(name = "infoTime")
    @CsvFormat(pattern = "yyyy-MM-dd")
    private Date infoDate;

}
