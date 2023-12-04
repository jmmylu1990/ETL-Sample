package com.example.model.dto.source.traffic.live;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.example.model.dto.etl.DetailModel;
import com.example.utils.annotation.AssignFrom;
import com.example.utils.annotation.CsvFormat;
import com.example.utils.annotation.CsvIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public @Data class VDLiveLinkFlowForCheck extends DetailModel implements Serializable {

	private static final long serialVersionUID = 1L;
	private int uuid = super.getUuid();
	private String vdID;
	private String authorityCode;
	private String linkID;
	@CsvIgnore
	private List<VDLiveLaneForCheck> lanes;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date dataCollectTime;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
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
