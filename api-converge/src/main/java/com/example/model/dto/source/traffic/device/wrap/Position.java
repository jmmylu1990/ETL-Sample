package com.example.model.dto.source.traffic.device.wrap;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

public @Data class Position implements Serializable{
	private static final long serialVersionUID = 1L;
	@JsonProperty("lng")
	private Double positionLon;
	@JsonProperty("lat")
	private Double positionLat;
}
