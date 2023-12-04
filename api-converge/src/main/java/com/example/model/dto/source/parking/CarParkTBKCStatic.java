package com.example.model.dto.source.parking;

import java.io.Serializable;
import java.util.Date;

import com.example.utils.annotation.AssignFrom;
import com.example.utils.annotation.CsvFormat;

import lombok.Data;

public @Data class CarParkTBKCStatic implements Serializable{

	private static final long serialVersionUID = 1L;
    
	private String id;
	private String ownerName;
	private String name;
	private Integer volumn;
	private Integer smallCarParkingSpace;
	private Integer largecarParkingSpace;
	private Integer motorcycleParkingSpace;
	private String areaID;
	private String areaName;
	private Double lat;
	private Double lng;
	private String location	;
	private String telephone;
	private String businessHours;
	private String chargeway;
	private Integer specialCharging;
	private Integer smallCarCharg;
	private Integer largeCarCharg;
	private Integer motorcycleCharg;
	private Integer smallCarChargeFrequency;
	private Integer largeCarChargeFrequency;
	private Integer motorcycleChargeFrequency;
	private Integer charg;
	private Integer chargeFrequency;
	private String parkingLotType;
	private Integer volumnTruck;
	private Integer volumnAuto;
	private Integer volumnScooter;
	private Integer volumnBike;
	private Integer volumnDisabledCar;
	private Integer volumnDisabledMoto;
	private String memo	;
	private Integer cate;
	private String apiKey;
	private String oldID;
	@AssignFrom(name = "updateTime")
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date srcUpdateTime;
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updateTime;
	@AssignFrom(name = "updateTime")
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date infoTime;
	@AssignFrom(name = "updateTime")
	@CsvFormat(pattern = "yyyy-MM-dd")
	private Date infoDate;

	public void setChargeway(String chargeway) {
		if(chargeway != null) {
			this.chargeway = chargeway.replaceAll("(\r\n|\n)", "");
		}else {
			this.chargeway = chargeway;
		}
		
	}
	
	public void setMemo(String memo) {
		if(memo != null) {
			this.memo = memo.replaceAll("(\r\n|\n)", "");
		}else {
			this.memo = memo;
		}
		
	}
	
	
}
