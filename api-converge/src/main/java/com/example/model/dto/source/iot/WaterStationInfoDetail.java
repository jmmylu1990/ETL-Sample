package com.example.model.dto.source.iot;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.example.model.dto.etl.DetailModel;
import com.example.utils.annotation.AssignFrom;
import com.example.utils.annotation.CsvFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
@EqualsAndHashCode(callSuper = true)
public @Data class WaterStationInfoDetail extends DetailModel{
	private String iowStationID;
	@JsonProperty("IoWPhysicalQuantityId")
	private String measIowPhysicalQuantityID;
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "GMT+8")
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonProperty("TimeStamp")
	private Date measTimestamp;
	@JsonProperty("Name")
	private String measName;
	@JsonProperty("SIUnit")
	private String measSIUnit;
	@JsonProperty("Value")
	private Double measValue;
	@AssignFrom(name = "meas_timestamp")
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date srcUpdateTime;
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updateTime;
	@AssignFrom(name = "measTimestamp")
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date infoTime;
	@AssignFrom(name = "srcUpdateTime")
	@CsvFormat(pattern = "yyyy-MM-dd")
	private Date infoDate;
	
	
}
