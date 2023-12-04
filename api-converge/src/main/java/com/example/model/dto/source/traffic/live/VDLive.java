package com.example.model.dto.source.traffic.live;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.example.utils.annotation.AssignFrom;
import com.example.utils.annotation.CsvFormat;
import com.example.utils.annotation.CsvIgnore;

import lombok.Data;

public @Data class VDLive implements Serializable {

	private static final long serialVersionUID = 1L;
	private String vdID;
	private String authorityCode;
	private String subAuthorityCode;
	private Integer updateInterval; 
	@CsvIgnore
	private List<VDLiveLinkFlow> linkFlows;
	private Integer status;
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "GMT+8")
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date dataCollectTime;
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
