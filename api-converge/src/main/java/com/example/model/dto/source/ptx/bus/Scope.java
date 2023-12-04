package com.example.model.dto.source.ptx.bus;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

public @Data class Scope implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@JsonProperty("Routes")
	private List<BusAlertKaoRoute> busAlertKaoRoutes;
}
