package com.example.model.dto.source.traffic.section;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.example.model.dto.source.traffic.RoadSection;
import com.example.model.dto.source.traffic.section.wrap.SectionMile;
import com.example.utils.annotation.AssignFrom;
import com.example.utils.annotation.CsvFormat;
import lombok.Data;
public @Data class SectionForTHB implements Serializable {

	private static final long serialVersionUID = 1L;

	private String sectionID;
	private String authorityCode;
	private String subAuthorityCode;
	private String sectionName;
	private String roadID;
	private String roadName;
	private Integer roadClass;
	private String roadDirection;
	private RoadSection roadSection;
	private SectionMile sectionMile;
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "GMT+8")
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date srcUpdateTime;
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "GMT+8")
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updateTime;
	@AssignFrom(name = "srcUpdateTime")
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date infoTime;
	@AssignFrom(name = "infoTime")
	@CsvFormat(pattern = "yyyy-MM-dd")
	private Date infoDate;

}