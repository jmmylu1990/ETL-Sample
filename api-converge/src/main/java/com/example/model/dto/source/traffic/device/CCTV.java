package com.example.model.dto.source.traffic.device;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.example.utils.annotation.AssignFrom;
import com.example.utils.annotation.CsvFormat;

import lombok.Data;

public @Data class CCTV implements Serializable {

	private static final long serialVersionUID = 1L;

	private String cctvID;
	private String authorityCode;
	private String subAuthorityCode;
	private String linkID;
	private String videoStreamURL;
	private String videoImageURL;
	private Integer imageRefreshRate;
	private Integer locationType;
	private Double positionLon;
	private Double positionLat;
	private Integer surveillanceType;
	private String surveillanceDescription;
	private String roadID;
	private String roadName;
	private Integer roadClass;
	private String roadDirection;
	private String roadSectionStart;
	private String roadSectionEnd;
	private String locationMile;
	private String layoutMapURL;
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "GMT+8")
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date srcUpdateTime;
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updateTime;
	@AssignFrom(name = "srcUpdateTime")
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date infoTime;
	@AssignFrom(name = "infoTime")
	@CsvFormat(pattern = "yyyy-MM-dd")
	private Date infoDate;

}
