package com.example.model.dto.source.other.bus;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.example.utils.annotation.AssignFrom;
import com.example.utils.annotation.CsvFormat;

import lombok.Data;

public @Data class BusReaktimePassengerKao implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@JsonProperty("TotalPassenger")
	private Integer totalPassenger;
	@JsonProperty("Routes")
	private Routes routes;
	
}
