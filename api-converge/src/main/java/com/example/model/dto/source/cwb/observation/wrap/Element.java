package com.example.model.dto.source.cwb.observation.wrap;

import java.io.Serializable;

import lombok.Data;

public @Data class Element implements Serializable {

	private static final long serialVersionUID = 1L;

	private String elementName;

	private String elementValue;

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(elementName).append("\t").append(elementValue);
		return builder.toString();
	}

}
