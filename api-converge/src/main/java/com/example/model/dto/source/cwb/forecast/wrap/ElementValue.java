package com.example.model.dto.source.cwb.forecast.wrap;

import java.io.Serializable;

import lombok.Data;

public @Data class ElementValue implements Serializable {

	private static final long serialVersionUID = 1L;

	private String value;
	
	private String measures;
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(value).append("\t").append(measures);
		return builder.toString();
	}

}
