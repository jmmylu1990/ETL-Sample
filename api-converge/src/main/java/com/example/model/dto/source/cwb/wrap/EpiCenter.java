package com.example.model.dto.source.cwb.wrap;

import java.io.Serializable;

import lombok.Data;

public @Data class EpiCenter implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private String location;
	private ValueWithUnit epiCenterLat;
	private ValueWithUnit epiCenterLon;
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(location).append("\t").append(epiCenterLat).append("\t").append(epiCenterLon);
		return builder.toString();
	}
}
