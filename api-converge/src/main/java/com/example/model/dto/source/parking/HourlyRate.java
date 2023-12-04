package com.example.model.dto.source.parking;

import java.io.Serializable;
import java.util.Date;

import com.example.model.dto.source.parking.warp.NameType;
import com.example.utils.annotation.AssignFrom;
import com.example.utils.annotation.CsvFormat;

import lombok.Data;

public @Data class HourlyRate implements Serializable{
	
	private static final long serialVersionUID = 1L;

	private String rateID;
	private NameType rateName;
	private String rateDescription;
	private Integer ratetrictionUserType;
	private Integer spaceType;
	private Integer freeMaxStay;
	private Integer rateQualifier;
	private Integer ratePrice;
	private Integer maxPrice;
	private Integer maxStay;
	private Integer minHafeHourChange;
	private String restriction;

}
