package com.example.model.dto.source.ptx.ubike.wrap;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public @Data class PointType implements Serializable {

	private static final long serialVersionUID = 1L;

	protected Double positionLon;

	protected Double positionLat;

	protected String geohash;

}
