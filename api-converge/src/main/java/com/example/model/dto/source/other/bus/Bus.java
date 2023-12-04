package com.example.model.dto.source.other.bus;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.example.utils.annotation.AssignFrom;
import com.example.utils.annotation.CsvFormat;

import lombok.Data;

public @Data class Bus implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private String routeID;
	@JsonProperty("id")
	private String busID;
	private Integer goBack;
	private String type;
	private Integer capacity;
	@JsonProperty("SRO_Capacity")
	private Integer sroCapacity;
	private Integer passenger;
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@AssignFrom(name = "updateTime")
	private Date srcUpdateTime;
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updateTime;
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@AssignFrom(name = "updateTime")
	private Date infoTime;
	@CsvFormat(pattern = "yyyy-MM-dd")
	@AssignFrom(name = "updateTime")
	private Date infoDate;

}
