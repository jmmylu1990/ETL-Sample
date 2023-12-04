package com.example.model.dto.source.traffic.live.wrap;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

public @Data class Car implements Serializable {
	private static final long serialVersionUID = 1L;
	@JsonProperty("carid")
	private String carID;
	private Integer volume;

}
