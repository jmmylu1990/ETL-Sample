package com.example.model.dto.source.cwb.forecast.wrap;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

public @Data class Element implements Serializable {

	private static final long serialVersionUID = 1L;

	private String elementName;
	
    private String description;
    
    @JsonProperty("time")
	private List<Time> times;
    
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(elementName).append("\t").append(description).append("\t").append(times);
		return builder.toString();
	}

}
