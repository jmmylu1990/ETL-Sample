package com.example.model.dto.source.cwb.observation.wrap;

import java.io.Serializable;

import lombok.Data;

public @Data class Paramter implements Serializable {

	private static final long serialVersionUID = 1L;

	private String parameterName;

	private String parameterValue;

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(parameterName).append("\t").append(parameterValue);
		return builder.toString();
	}

}
