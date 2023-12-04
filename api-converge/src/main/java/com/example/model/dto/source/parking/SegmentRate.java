package com.example.model.dto.source.parking;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

public @Data class SegmentRate implements Serializable{
	private static final long serialVersionUID = 1L;
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "GMT+8")
	private Date updateTime;
	private Integer updateInterval;
	private String authorityCode;
	private List<ParkingRate> parkingRates;

}
