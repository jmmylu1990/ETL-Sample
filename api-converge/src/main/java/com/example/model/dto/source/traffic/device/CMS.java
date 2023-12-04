package com.example.model.dto.source.traffic.device;

import java.io.Serializable;
import java.util.Date;

import com.example.model.dto.source.traffic.RoadSection;
import com.example.utils.annotation.AssignFrom;
import com.example.utils.annotation.CsvFormat;

import lombok.Data;

public @Data class CMS implements Serializable {

	private static final long serialVersionUID = 1L;

	private String cmsID;
	private String authorityCode;
	private String subAuthorityCode;
	private Integer updateInterval;
	private String linkID;
	private Integer locationType;
	private Double positionLon;
	private Double positionLat;
	private String roadID;
	private String roadName;
	private Integer roadClass;
	private String roadDirection;
	private RoadSection roadSection = new RoadSection();
	private String locationMile;
	private String layoutMapURL;
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date srcUpdateTime;
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updateTime;
	@AssignFrom(name = "srcUpdateTime")
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date infoTime;
	@CsvFormat(pattern = "yyyy-MM-dd")
	@AssignFrom(name = "srcUpdateTime")
	private Date infoDate;


}
