package com.example.model.dto.source.iot.device;

import java.io.Serializable;

import lombok.Data;

public @Data class Value implements Serializable{
	private static final long serialVersionUID = 1L;
	private Integer southPeopleFlowCounts;
	private Integer southDataCounts;	
	private Integer northPeopleFlow_counts;	
	private Integer northDataCounts;
}
