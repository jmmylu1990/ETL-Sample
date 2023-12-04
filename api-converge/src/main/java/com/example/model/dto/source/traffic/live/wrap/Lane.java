package com.example.model.dto.source.traffic.live.wrap;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

public @Data class Lane implements Serializable{
	private static final long serialVersionUID = 1L;
	@JsonProperty("vsrdir")
	private String vsrDir;
	@JsonProperty("vsrid")
	private String vsrID;
	private Double speed;
	private Double laneoccupy;
	private List<Car> cars;

}
