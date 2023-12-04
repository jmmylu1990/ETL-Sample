package com.example.model.dto.source.other.bus;

import java.io.Serializable;
import java.util.Date;

import com.example.utils.annotation.AssignFrom;
import com.example.utils.annotation.CsvFormat;

import lombok.Data;

public @Data class Route implements Serializable{
	
	private static final long serialVersionUID = 1L;

	private String id;
	private Buses buses;
}
