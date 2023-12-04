	package com.example.model.dto.source.iot;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.example.utils.annotation.AssignFrom;
import com.example.utils.annotation.CsvFormat;
import com.example.utils.annotation.CsvIgnore;
import lombok.Data;

public @Data class WaterStationInfoMaster implements Serializable {
	private static final long serialVersionUID = 1L;
    
	
	private String iowStationID;
	private String stationID;
	@JsonProperty("Name")
	private String stationName;
	private String countyCode;
	private String countyName;
	private String townCode;
	private String townName;
	private String basinCode;
	private String basinName;
	private Double latitude;
	private Double longtiude;
	private String adminName;
	@CsvIgnore
	@JsonProperty("Measurements")
	private List<WaterStationInfoDetail> waterStationInfoDetails;

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

	@JsonProperty(value = "Measurements")
	public void setWaterStationInfoDetails(List<WaterStationInfoDetail> waterStationInfoDetails) {

		for(WaterStationInfoDetail waterStationInfoDetail :waterStationInfoDetails) {
			waterStationInfoDetail.setIowStationID(this.iowStationID);
			this.srcUpdateTime= waterStationInfoDetail.getMeasTimestamp();
		}
		this.waterStationInfoDetails = waterStationInfoDetails;
	}

	public void setStationName(String stationName){
			 this.stationName = stationName.replaceAll("\t|\r|\n", "");
	}
	
	
	
}
