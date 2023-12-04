package com.example.model.dto.source.parking.warp;

import java.io.Serializable;

import lombok.Data;

public @Data class Position implements Serializable{
	
	private static final long serialVersionUID = 1L;

	private Double positionLat;
	private Double positionLon;
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(positionLat).append("\t").append(positionLon);
		return builder.toString();
	}
	
	
}
