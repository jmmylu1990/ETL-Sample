package com.example.model.dto.source.parking;

import java.io.Serializable;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.example.model.dto.source.parking.warp.NameType;
import com.example.model.dto.source.parking.warp.Position;
import com.example.utils.JsonUtils;
import com.example.utils.annotation.AssignFrom;
import com.example.utils.annotation.CsvFormat;

import lombok.Data;

public @Data class CarParkInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	private String carParkID;
	private NameType carParkName;
	private String operatorID;
	private String description;
	private Integer parkingGuideType;
	private Integer parkingType;
	private Integer parkingSiteType;
	private Integer chargType;
	// private ChargType chargTypes;
	private String telePhone;
	private Position carParkPosition;
	private String email;
	private String address;
	private String fareDescription;
	private Integer isFreeParkingOutOfHours;
	private Integer isPublic;
	private Integer operationType;
	private Integer liveOccupancyAvailable;
	private Integer evrEchargingAvailable;
	private Integer monthlyTicketAvailable;
	private Integer reservationAvailable;
	private Integer wheelchairAccessible;
	private Integer overnightPermitted;
	private Integer ticketMachine;
	private Integer toilet;
	private Integer restaurant;
	private Integer gasstation;
	private Integer shop;
	private Integer gated;
	private Integer lighting;
	private Integer secureParking;
	private Integer ticketOffice;
	private Integer prohibitedForAnyHazardousMaterialLoads;
	private Integer securityGuard;
	private String city;
	private String cityCode;
	private String townName;
	private String townID;
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

	@JsonProperty("parkingTypes")
	public void setParkingTypes(Object parkingTypes) throws JsonMappingException, JsonProcessingException {
		Integer parkingType = JsonUtils.getMapper().readTree(parkingTypes.toString().replace("=", ":"))
				.get("ParkingType").asInt();
		this.parkingType = parkingTypes == null ? null : parkingType;
	}

	@JsonProperty("parkingSiteTypes")
	public void setParkingSiteTypes(Object parkingSiteTypes) throws JsonMappingException, JsonProcessingException {
		Integer parkingSiteType = JsonUtils.getMapper().readTree(parkingSiteTypes.toString().replace("=", ":"))
				.get("ParkingSiteType").asInt();

		this.parkingSiteType = parkingSiteTypes == null ? null : parkingSiteType;
	}

	@JsonProperty("ChargeTypes")
	public void setChargTypes(Object chargeTypes) throws JsonMappingException, JsonProcessingException {
		Integer chargType = JsonUtils.getMapper().readTree(chargeTypes.toString().replace("=", ":")).get("ChargeType")
				.asInt();
		this.chargType = chargeTypes == null ? null : chargType;
	}

}
