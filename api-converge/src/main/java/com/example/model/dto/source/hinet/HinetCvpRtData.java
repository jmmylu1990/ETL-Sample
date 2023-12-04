package com.example.model.dto.source.hinet;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.example.utils.annotation.AssignFrom;
import com.example.utils.annotation.CsvFormat;

import lombok.Data;

public @Data class HinetCvpRtData implements Serializable {
	private static final long serialVersionUID = 1L;

	private String apiID;
	private String status;
	private String msg;
	private String name;
	private String gid;
	private Integer population;
	@JsonFormat(pattern = "yyyyMMddHHmmss", timezone = "GMT+8")
	@CsvFormat(pattern = "yyyyMMddHHmmss")
	private Date dataTime;
	@AssignFrom(name = "dataTime")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date srcUpdateTime;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updateTime;
	@AssignFrom(name = "dataTime")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date infoTime;
	@AssignFrom(name = "dataTime")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	@CsvFormat(pattern = "yyyy-MM-dd")
	private Date infoDate;

}
