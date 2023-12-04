package com.example.model.dto.source.parking;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.example.model.dto.source.parking.warp.NameType;
import com.example.utils.JsonUtils;
import com.example.utils.annotation.AssignFrom;
import com.example.utils.annotation.CsvFormat;

import lombok.Data;

public @Data class CurbParkingSegmentSpace implements Serializable {

	private static final long serialVersionUID = 1L;

	private String parkingSegmentID;
	private NameType parkingSegmentName;
	private Integer totalSpaces;
	private Integer spaceType;
	private Integer hasChargingPoint;
	private Integer numberOfSpaces;
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

	@JsonProperty("Spaces")
	public void setSpaces(Object Spaces) throws JsonMappingException, JsonProcessingException {
		JsonNode jsonNode = JsonUtils.getMapper().readTree(Spaces.toString().replace("=", ":")).get("Space");
		this.spaceType = jsonNode.get("SpaceType") == null ? null : jsonNode.get("SpaceType").asInt();
		this.hasChargingPoint = jsonNode.get("HasChargingPoint") == null ? null : jsonNode.get("HasChargingPoint").asInt();
		this.numberOfSpaces = jsonNode.get("NumberOfSpaces") == null ? null : jsonNode.get("NumberOfSpaces").asInt();

	}
}
