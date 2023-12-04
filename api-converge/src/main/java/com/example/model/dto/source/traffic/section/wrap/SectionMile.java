package com.example.model.dto.source.traffic.section.wrap;

import java.io.Serializable;

import lombok.Data;

public @Data class SectionMile implements Serializable{
	private static final long serialVersionUID = 1L;
	private String startKM;
	private String endKM;
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(startKM).append("\t").append(endKM);
		return builder.toString();
	}
}
