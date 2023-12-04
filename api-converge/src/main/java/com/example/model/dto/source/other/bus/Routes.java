package com.example.model.dto.source.other.bus;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

public @Data class Routes implements Serializable {

	private static final long serialVersionUID = 1L;

	@JsonProperty("Route")
	private List<Route> route;

}
