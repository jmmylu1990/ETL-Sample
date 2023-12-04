package com.example.model.dto.source.tdcs;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.example.utils.annotation.AssignFrom;
import com.example.utils.annotation.CsvFormat;

import lombok.Data;

public @Data class  NfbEtagM07 implements java.io.Serializable{

	private static final long serialVersionUID = 1L;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date timeInterval;
	private String gantryFrom;
	private String vehicleType;
	private Double tripLength;
	private Integer volume;
	private String authority;
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@AssignFrom(name = "timeInterval")
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
