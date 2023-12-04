package com.example.model.dto.source.mrt;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.example.model.dto.source.ptx.ubike.wrap.NameType;
import com.example.utils.annotation.AssignFrom;
import com.example.utils.annotation.CsvFormat;

import lombok.Data;

public @Data class MrtLiveBoard implements Serializable {
	private static final long serialVersionUID = 1L;
    

	private String lineNo;

	private String lineID;

	private NameType lineName;

	private String stationID;
	
	private NameType stationName;

	private String tripHeadSign;
	@Column(name="destination_staion_id")
	private String destinationStaionID;
	@Column(name="destination_station_id")
	private String destinationStationID;
	private NameType destinationStationName;

	private Integer estimateTime;
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "GMT+8")
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date srcUpdateTime;
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "GMT+8")
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updateTime;
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date infoTime;
	@CsvFormat(pattern = "yyyy-MM-dd")
	private Date infoDate;
	
	

}
