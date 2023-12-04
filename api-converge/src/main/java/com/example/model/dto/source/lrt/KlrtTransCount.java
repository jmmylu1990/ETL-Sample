package com.example.model.dto.source.lrt;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.example.utils.annotation.AssignFrom;
import com.example.utils.annotation.CsvFormat;

import lombok.Data;

public @Data class KlrtTransCount implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private String ticketType;	
	private String trainType;	
	private String stationNo;	
	private String stationName;	
	@JsonFormat(pattern = "yyyyMMdd HH:mm:ss", timezone = "GMT+8")
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date transactionDate;	
	@JsonProperty("Enter")
	private Integer enterCount;	
	@JsonProperty("Exit")
	private Integer exitCount;	
	@AssignFrom(name = "updateTime")
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date srcUpdateTime;	
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updateTime;	
	@AssignFrom(name = "transactionDate")
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date infoTime;	
	@AssignFrom(name = "transactionDate")
	@CsvFormat(pattern = "yyyy-MM-dd")
	private Date infoDate;	

}
