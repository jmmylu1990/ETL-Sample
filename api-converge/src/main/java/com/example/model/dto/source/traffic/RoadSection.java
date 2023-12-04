package com.example.model.dto.source.traffic;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

public @Data class RoadSection implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@JsonProperty("Start")
	private String start;
	
	@JsonProperty("End")
	private String end;

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(start).append("\t").append(end);
		return builder.toString();
	}

}
