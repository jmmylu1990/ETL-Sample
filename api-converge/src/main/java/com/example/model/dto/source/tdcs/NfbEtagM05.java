package com.example.model.dto.source.tdcs;

import java.util.Date;

import com.example.utils.annotation.AssignFrom;
import com.example.utils.annotation.CsvFormat;

import lombok.Data;

public @Data class  NfbEtagM05 implements java.io.Serializable{

	private static final long serialVersionUID = 1L;
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date timeInterval;
	private String gantryFrom;
	private String gantryTo;
	private String vehicleType;
	private Integer speedMeanSpeed;
	private Integer volume;
	private String authority;
	@AssignFrom(name = "timeInterval")
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
