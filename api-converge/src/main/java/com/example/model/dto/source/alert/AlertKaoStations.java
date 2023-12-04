package com.example.model.dto.source.alert;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.example.model.dto.etl.DetailModel;
import com.example.utils.annotation.CsvFormat;

import lombok.Data;
import lombok.EqualsAndHashCode;
@EqualsAndHashCode(callSuper = false)
public @Data class AlertKaoStations extends DetailModel implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private String alertID;
	private String stationID;
	private String stationName;
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date srcUpdateTime;
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updateTime;
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date infoTime;
	@CsvFormat(pattern = "yyyy-MM-dd")
	private Date infoDate;

}
