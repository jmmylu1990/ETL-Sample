package com.example.model.dto.source.ptx.ubike;

import java.io.Serializable;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.example.model.dto.source.ptx.ubike.wrap.NameType;
import com.example.model.dto.source.ptx.ubike.wrap.PointType;
import com.example.utils.annotation.AssignFrom;
import com.example.utils.annotation.CsvFormat;
import lombok.Data;

public @Data class UbikeStationPTX implements Serializable {

	private static final long serialVersionUID = 1L;

	private String stationUID;
	private String stationID;
	private String authorityID;
	private NameType stationName;
	private PointType stationPosition;
	private NameType stationAddress;
	private Integer bikesCapacity;
	private Integer serviceType;
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "GMT+8")
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
