package com.example.model.dto.source.traffic.live;

import java.io.Serializable;
import java.util.Date;

import com.example.model.dto.etl.DetailModel;
import com.example.utils.annotation.AssignFrom;
import com.example.utils.annotation.CsvFormat;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public @Data class CMSLiveMessage extends DetailModel implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = 1L;
	private int uuid = super.getUuid();
	private String cmsID;
	private String authorityCode;
	private String text;
	private String image;
	private Integer type;
	private Integer priority;
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	protected Date srcUpdateTime;
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	protected Date updateTime;
	@AssignFrom(name = "srcUpdateTime")
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	protected Date infoTime;
	@AssignFrom(name = "srcUpdateTime")
	@CsvFormat(pattern = "yyyy-MM-dd")
	protected Date infoDate;

}
