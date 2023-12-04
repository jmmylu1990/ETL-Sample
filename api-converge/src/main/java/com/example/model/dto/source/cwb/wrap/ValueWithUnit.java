package com.example.model.dto.source.cwb.wrap;

import java.io.Serializable;

import lombok.Data;

public @Data class ValueWithUnit implements Serializable {

	private static final long serialVersionUID = 1L;

	private Double value;
	
	private String unit;
	
	@Override
	public String toString() {
		return String.valueOf(value);
	}
	
	
}
