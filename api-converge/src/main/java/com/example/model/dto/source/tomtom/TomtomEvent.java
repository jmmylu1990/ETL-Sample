package com.example.model.dto.source.tomtom;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.example.utils.annotation.AssignFrom;
import com.example.utils.annotation.CsvFormat;

import lombok.Data;

public @Data class TomtomEvent implements Serializable{
	
	private static final long serialVersionUID = 1L;

	private Integer zoneID;
	private String id;
	private Position p;
	private String ic;
	private String ty;
	private String cs;
	private String d;
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'",timezone = "GMT+8")
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date sd;
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'",timezone = "GMT+8")
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date ed;
	private String f;
	private String t;
	private Integer l;
	private Integer dl;
	private String r;
	private String v;
	private String lineString;

	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date srcUpdateTime;
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updateTime;
	@AssignFrom(name = "sd")
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date infoTime;
	@AssignFrom(name = "sd")
	@CsvFormat(pattern = "yyyy-MM-dd")
	private Date infoDate;
	
}
