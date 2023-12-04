package com.example.model.dto.source.parking;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.example.utils.annotation.AssignFrom;
import com.example.utils.annotation.CsvFlatten;
import com.example.utils.annotation.CsvFormat;
import com.example.utils.enums.FlatModeEnum;

import lombok.Data;

public @Data class ParkingRate implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@CsvFlatten
	private List<HourlyRate> hourlyRates;
	
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date srcUpdateTime;
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updateTime;
	@AssignFrom(name = "srcUpdateTime")
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date infoTime;
	@AssignFrom(name = "srcUpdateTime")
	@CsvFormat(pattern = "yyyy-MM-dd")
	private Date infoDate;
}
