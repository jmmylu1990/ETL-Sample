package com.example.model.dto.source.iot;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.example.utils.annotation.AssignFrom;
import com.example.utils.annotation.CsvFormat;

import lombok.Data;

public @Data class RoadEvents implements Serializable {
	private static final long serialVersionUID = 1L;

	
	private String authorityOID;
	private String uid;
	private String authorityCode;
	private String authorityName;
	private String telephone;
	private String email;
	private String webURL;
	private String gisArea;
	private Integer referenceType;
	private String locationDescription;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date controlStartTime;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date controlEndTime;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date expectedStartTime;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date expectedEndTime;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date actualStartTime;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date actualEndTime;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date versionUpdateTime;
	private String eventType;
	private String eventType2;
	private String title;
	private String description;
	private String source;
	private Integer severity;
	private String impactGisArea;
	private String roadName;
	private String restrictedLanes;
	private String involvedVehicle;
	private String deathPeople;
	private String injuredPeople;
	private String linkID;
	private String bearing;
	private String roadDirectionID;
	private String roadClass;
	private String constructionPetitioner;
	private String constructionApprovalNo;
	private String activityPetitioner;
	private String activityApprovalno;
	private String controlLocationDescription;
	private String causeEvent;
	@AssignFrom(name = "versionUpdateTime")
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

	public void setDescription(String description) {
		if (description != null) {
			this.description = description.replaceAll("\r|\n", "");

		}

	}

}
