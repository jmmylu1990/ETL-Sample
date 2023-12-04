package com.example.model.dto.source.parking;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.JsonNode;
import com.example.utils.annotation.AssignFrom;
import com.example.utils.annotation.CsvFormat;

import lombok.Data;

public @Data class CarParkTBKCAvailability implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;
	private Integer volumn;
	private Integer leftSpace;
	private Integer smallCarVacancy;
	private Integer largecarVacancy;
	private Integer motorcycleVacancy;
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date srcUpdateTime;
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updateTime;
	@AssignFrom(name = "srcUpdateTime")
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date infoTime;
	@AssignFrom(name = "srcUpdateTime")
	@CsvFormat(pattern = "yyyy-MM-dd")
	private Date infoDate;

	@JsonSetter("leftspace")
	public void setLeftSpace(JsonNode jsonNode) {
		this.leftSpace = jsonNode.asText().matches("\\-?[0-9]{1,4}") ? jsonNode.asInt() : -99;

	}

	// 來源LeftSpace有時候會null值，實際上應該為-1
	public void setLeftSpace(Integer leftSpace) {
		this.leftSpace = leftSpace == null ? -1 : leftSpace;
	}
}
