package com.example.model.dto.source.ptx.bus;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.JsonNode;
import com.example.utils.annotation.AssignFrom;
import com.example.utils.annotation.CsvFormat;

import lombok.Data;

public @Data class BusA1Kao implements Serializable {

	private static final long serialVersionUID = 1L;

	private String plateNumb;
	private String operatorID;
	private String routeUID;
	private String routeID;
	private String routeNameZh;
	private String routeNameEn;
	private String subRouteUID;
	private String subRouteID;
	private String subRouteNameZh;
	private String subRouteNameEn;
	private Integer direction;
	private Double positionLon;	
	private Double positionLat;	
	private String geohash;	
	private Double speed;	
	private Double azimuth;	
	private Integer dutyStatus;
	private Integer busStatus;
	private String messageType;
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "GMT+8")
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date gpsTime;
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "GMT+8")
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date srcUpdateTime;
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonIgnore
	private Date updateTime;
	@AssignFrom(name = "srcUpdateTime")
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date infoTime;
	@AssignFrom(name = "infoTime")
	@CsvFormat(pattern = "yyyy-MM-dd")
	private Date infoDate;

	@JsonSetter("RouteName")
	public void setRouteName(JsonNode node) {
		this.setRouteNameZh(node.path("Zh_tw").asText());
		this.setRouteNameEn(node.path("En").asText());

	}

	@JsonSetter("SubRouteName")
	public void setSubRouteName(JsonNode node) {
		this.setSubRouteNameZh(node.path("Zh_tw").asText());
		this.setSubRouteNameEn(node.path("En").asText());
	}

	@JsonSetter("BusPosition")
	public void setBusPosition(JsonNode node) {
		this.setPositionLon(node.path("PositionLon").asDouble());
		this.setPositionLat(node.path("PositionLat").asDouble());
		this.setGeohash(node.path("GeoHash").asText());

	}

}
