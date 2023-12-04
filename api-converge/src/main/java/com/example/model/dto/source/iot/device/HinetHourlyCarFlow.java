package com.example.model.dto.source.iot.device;

import java.io.Serializable;
import java.util.Date;

import com.example.utils.annotation.AssignFrom;
import com.example.utils.annotation.CsvFormat;

import lombok.Data;

public @Data class HinetHourlyCarFlow implements Serializable{

	private static final long serialVersionUID = 1L;

	private String id;	
	private String deviceID;	
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date dataTime;
	private Integer motoStraightCounts;	
	private Integer motoLeftCounts;	
	private Integer motoRightCounts;	
	private Integer carStraightCounts;	
	private Integer carLeftCounts;	
	private Integer carRightCounts;	
	private Integer truckStraightCounts;	
	private Integer truckLeftCounts;	
	private Integer truckRightCounts;	
	private Integer totalCounts;	
	@AssignFrom(name = "dataTime")
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date srcUpdateTime;	
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updateTime;	
	@AssignFrom(name = "dataTime")
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date infoTime;	
	@AssignFrom(name = "dataTime")
	@CsvFormat(pattern = "yyyy-MM-dd")
	private Date infoDate;	
}
