package com.example.model.dto.source.traffic.live;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.example.model.dto.source.traffic.section.wrap.DataSources;
import com.example.utils.annotation.AssignFrom;
import com.example.utils.annotation.CsvFormat;

import lombok.Data;

public @Data class LivetrafficSectionID implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String uuid = UUID.randomUUID().toString();
	private String authorityCode;
	private String sectionID;
	private Integer travelTime;
	private Double travelSpeed;
	private String congestionLevelID;
	private String congestionLevel;
	private DataSources dataSources;
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "GMT+8")
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date dataCollectTime;
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "GMT+8")
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date srcUpdateTime;
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updateTime;
	@AssignFrom(name = "dataCollectTime")
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date infoTime;
	@CsvFormat(pattern = "yyyy-MM-dd")
	@AssignFrom(name = "dataCollectTime")
	private Date infoDate;

}
