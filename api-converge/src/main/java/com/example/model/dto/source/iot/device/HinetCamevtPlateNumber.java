package com.example.model.dto.source.iot.device;

import java.io.Serializable;
import java.util.Date;

import com.example.utils.annotation.AssignFrom;
import com.example.utils.annotation.CsvFormat;

import lombok.Data;

public @Data class HinetCamevtPlateNumber implements Serializable{

	private static final long serialVersionUID = 1L;

	private String id;
	private String deviceID;
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date dataTime;
	private String snapshotURL;
	private Integer version;
	private String cameraID;
	private String cameraName;
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date eventTime;
	private String plateText;
	private String vehicleType;
	private String vehicleColor;
	@AssignFrom(name = "dataTime")
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date srcUpdateTime;
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updateTime;
	@AssignFrom(name = "dataTime")
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date infoTime;
	@AssignFrom(name = "dataTime")
	@CsvFormat(pattern = "yyyy-MM-dd")
	private Date infoDate;
}
