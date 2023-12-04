package com.example.model.dto.source.parking;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.example.model.dto.source.parking.warp.ImageURL;
import com.example.model.dto.source.parking.warp.NameType;
import com.example.model.dto.source.parking.warp.Position;
import com.example.model.dto.source.parking.warp.RoadSection;
import com.example.utils.JsonUtils;
import com.example.utils.annotation.AssignFrom;
import com.example.utils.annotation.CsvFormat;

import lombok.Data;

public @Data class CurbParkingSegment implements Serializable {

	private static final long serialVersionUID = 1L;

	private String parkingSegmentID;
	private NameType parkingSegmentName;
	private RoadSection roadSection;
	private Position parkingSegmentPosition;
	private String geometry;
	private String description;
	private String imageURL;
	@JsonProperty("FareDescription")
	private String fareDescription;
	private String specialOfferDescription;
	private Integer hasChargingPoint;
	private String city;
	private String cityCode;
	private String townName;
	private String townID;
	private String landmark;
	private String road;
	private String link;
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
	
}
