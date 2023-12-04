package com.example.model.dto.source.traffic.device;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.example.model.dto.source.traffic.RoadSection;
import com.example.utils.annotation.AssignFrom;
import com.example.utils.annotation.CsvFormat;
import com.example.utils.annotation.CsvIgnore;

import lombok.Data;

public @Data class VDForKHH implements Serializable {

	private static final long serialVersionUID = 1L;

	private String vdID;
	private String authorityCode;
	private String subAuthorityCode;
	@JsonProperty(value = "BiDirectional")
	private Integer biDirectional;
	
	@CsvIgnore
	private List<VDDetectionlinkForKHH> detectionlinks;
	private Integer vdType;
	private Integer locationType;
	private Integer detectionType;
	private Double positionLon;
	private Double positionLat;
	private String roadID;
	private String roadName;
	private Integer roadClass;
	private RoadSection roadSection;
	private String locationMile;
	private String layoutMapURL;
//	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
//	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//	private Date srcUpdateTime;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updateTime;
//	@AssignFrom(name = "srcUpdateTime")
//	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//	private Date infoTime;
//	@AssignFrom(name = "srcUpdateTime")
//	@CsvFormat(pattern = "yyyy-MM-dd")
//	private Date infoDate;

}
