package com.example.model.dto.source.iot;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.JsonNode;
import com.example.utils.annotation.AssignFrom;
import com.example.utils.annotation.CsvFormat;

import lombok.Data;

public @Data class RoadData implements Serializable {

	private static final long serialVersionUID = 1L;

	@JsonProperty("number")
	private String eventNumber;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date keyTime;

	private String status;

	private String region;

	@JsonAlias("updatetime")
	@JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date dataUpdateTime;

	private String roadType;

	@JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date happenTime;

	@JsonFormat(pattern = "yyyy/M/d HH:mm", timezone = "GMT+8")
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date continueTime;

	private String direction;

	private Integer speedLow;

	private Integer speedTop;

	private String road1;

	private String from1;

	private String to1;

	private String road2;

	private String from2;

	private String to2;

	private String comment;

	private String messageSrc;

	private String srcDetail;

	@JsonFormat(pattern = "yyyy/M/d HH:mm", timezone = "GMT+8")
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date cancelTime;

	private Double x1;

	private Double y1;

	private Double x2;

	private Double y2;

	private Double twd67x1;

	private Double twd67y1;

	private Double twd67x2;

	private Double twd67y2;

	@JsonProperty("name")
	private String eventName;

	private String stationSn;

	private String areaSn;

	private String area;

	private Double fromKm;

	private Double toKm;

	private Integer eventLevel;

	private String affect;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date srcUpdateTime;

	@JsonIgnore
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updateTime;

	@JsonIgnore
	@AssignFrom(name = "happenTime")
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date infoTime;

	@JsonIgnore
	@AssignFrom(name = "happenTime")
	@CsvFormat(pattern = "yyyy-MM-dd")
	private Date infoDate;

	@JsonSetter("GPS")
	public void setGPS(JsonNode node) {
		this.setX1(node.path("X1").asDouble());
		this.setY1(node.path("Y1").asDouble());
		this.setX2(node.path("X2").asDouble());
		this.setY2(node.path("Y2").asDouble());
	}

	@JsonSetter("TWD67")
	public void setTWD67(JsonNode node) {
		this.setTwd67x1(node.path("TWD67X1").asDouble());
		this.setTwd67y1(node.path("TWD67Y1").asDouble());
		this.setTwd67x2(node.path("TWD67X2").asDouble());
		this.setTwd67y2(node.path("TWD67Y2").asDouble());
	}

}
