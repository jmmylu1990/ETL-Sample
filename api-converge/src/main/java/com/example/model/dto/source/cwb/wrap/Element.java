package com.example.model.dto.source.cwb.wrap;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.Data;

public @Data class Element implements Serializable {

	private static final long serialVersionUID = 1L;

	private String elementName;

	private String elementValue;

	@JsonSetter("elementValue")
	public void setElementValue(JsonNode jsonNode) {

		this.elementValue = jsonNode.asText();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(elementName).append("\t").append(elementValue);
		return builder.toString();
	}

}