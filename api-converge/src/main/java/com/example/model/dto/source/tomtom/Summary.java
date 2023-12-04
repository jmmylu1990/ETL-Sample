package com.example.model.dto.source.tomtom;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

public @Data class Summary implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer lengthInMeters;
	private Integer travelTimeInSeconds;
	private Integer trafficDelayInSeconds;
	private Integer trafficLengthInMeters;
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "GMT+8")
	private Date departureTime;
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "GMT+8")
	private Date arrivalTime;
}
