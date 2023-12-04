package com.example.model.dto.source.other.bus;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

public @Data class Buses implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private List<Bus> bus;

}
