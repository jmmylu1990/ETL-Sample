package com.example.model.dto.source.parking;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.example.utils.annotation.AssignFrom;
import com.example.utils.annotation.CsvFormat;

import lombok.Data;

public @Data class OutsideParkingInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	private String area;
	private String type;
	private String parkName;
	private String address;
	private Double lon;
	private Double lat;
	private Integer totalLargeCar;
	private Integer totalSmallCar;
	private Integer totalMotor;
	private Integer totalBike;
	private String payex;
	private String remark;

	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date srcUpdateTime;
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "GMT+8")
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updateTime;
	@AssignFrom(name = "srcUpdateTime")
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date infoTime;
	@AssignFrom(name = "infoTime")
	@CsvFormat(pattern = "yyyy-MM-dd")
	private Date infoDate;

}
