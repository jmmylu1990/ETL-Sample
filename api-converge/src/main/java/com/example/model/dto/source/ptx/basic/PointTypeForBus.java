package com.example.model.dto.source.ptx.basic;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public @Data class PointTypeForBus implements Serializable {

	private static final long serialVersionUID = 1L;

	protected Double positionLat;
	protected Double positionLon;
	protected String geohash;

}
