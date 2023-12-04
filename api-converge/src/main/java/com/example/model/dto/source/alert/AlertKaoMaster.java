package com.example.model.dto.source.alert;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.example.utils.annotation.CsvFormat;
import com.example.utils.annotation.CsvIgnore;

import lombok.Data;

public @Data class AlertKaoMaster implements Serializable{
	private static final long serialVersionUID = 1L;

	private String alertID;
	private String title;
	private String description;
	private Integer status;
	private Integer direction;
	private Integer level;
	private Integer effect;
	private String reason;
	private String alertURL;
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "GMT+8")
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date startTime;
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "GMT+8")
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date endTime;
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "GMT+8")
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date publishTime;
	@CsvIgnore
	private Scope scope;
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "GMT+8")
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date srcUpdateTime;
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "GMT+8")
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updateTime;
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "GMT+8")
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date infoTime;
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	@CsvFormat(pattern = "yyyy-MM-dd")
	private Date infoDate;
	
	
	
}
