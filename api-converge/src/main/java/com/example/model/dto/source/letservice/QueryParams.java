package com.example.model.dto.source.letservice;

import java.io.Serializable;

import lombok.Data;

public @Data class QueryParams implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private String techlawsid;
	private String plateNumber;
	private String sDate;
	private String eDate;
	private Integer beenverified;
	private Integer page;
	private Integer pageSize;
	private Integer sortSet;


}
