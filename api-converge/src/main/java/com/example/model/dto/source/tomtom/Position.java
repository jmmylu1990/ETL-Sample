package com.example.model.dto.source.tomtom;

import java.io.Serializable;

import lombok.Data;

public @Data class Position implements Serializable{

	private static final long serialVersionUID = 1L;
	private Double x;
	private Double y;
}
