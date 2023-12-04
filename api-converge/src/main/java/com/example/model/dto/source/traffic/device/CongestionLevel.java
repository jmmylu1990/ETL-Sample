package com.example.model.dto.source.traffic.device;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.example.utils.annotation.AssignFrom;
import com.example.utils.annotation.CsvFormat;
import com.example.utils.annotation.CsvIgnore;

import lombok.Data;

public @Data class CongestionLevel implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name = "PK_CongestionLevelID")
	private String congestionLevelID;
	
	@Column(name = "PK_AuthorityCode")
	private String authorityCode;

	private String subAuthorityCode;

	private String congestionLevelName;

	private String description;

	private String measureIndex;

	@CsvIgnore
	private List<CongestionLevelItem> levels;

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
