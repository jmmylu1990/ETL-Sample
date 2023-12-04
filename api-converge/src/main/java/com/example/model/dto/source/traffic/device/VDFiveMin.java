package com.example.model.dto.source.traffic.device;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.example.utils.annotation.AssignFrom;
import com.example.utils.annotation.CsvFormat;

import lombok.Data;

public @Data class VDFiveMin implements Serializable{
	
	private static final long serialVersionUID = 1L;

	private String vdID;
	private String status;
	private String linkID;
	private Double speed;
	private Integer sVolume;
	private Integer tVolume;
	private Integer lVolume;
	private Integer mVolume;
	private Double occ;
	private Double tti;
	private Double pcu;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date dataCollectTime;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date srcUpdateTime;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updateTime;
	@AssignFrom(name = "srcUpdateTime")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date infoTime;
	@AssignFrom(name = "srcUpdateTime")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	@CsvFormat(pattern = "yyyy-MM-dd")
	private Date infoDate;
}
