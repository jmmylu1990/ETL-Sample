package com.example.model.dto.source.ptx.klrt;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.example.utils.annotation.AssignFrom;
import com.example.utils.annotation.CsvFormat;
import com.example.model.dto.source.ptx.ubike.wrap.NameType;
import com.example.model.dto.source.ptx.ubike.wrap.PointType;
import lombok.Data;

public @Data class KlrtStationInfo implements Serializable{
	private static final long serialVersionUID = 1L;

	private String stationUID;
	private String stationID;
	private NameType StationName;
	private String stationAddress;
	private boolean bikeAllowOnHoliday;
	private Integer versionID;
	private PointType StationPosition;
	private String locationCity;
	private String locationCityCode;
	private String locationTown;
	private String locationTownCode;
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
