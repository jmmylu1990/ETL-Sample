package com.example.model.dto.source.hinet;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.example.utils.annotation.AssignFrom;
import com.example.utils.annotation.CsvFormat;

import lombok.Data;

public @Data class HinetCvpTwrtdata implements Serializable {
	private static final long serialVersionUID = 1L;
	private String apiID;
	private String status;	
	private String msg;
	private String name;	
	private Integer population;
	private Integer male;
	private Integer female;
	private Integer age0019;
	private Integer age2029;
	private Integer age3039;
	private Integer age4049;
	private Integer age5059;
	private Integer age6099;
	@JsonProperty("national")
	private String nation;
	private Integer a;
	private Integer b;
	private Integer c;
	private Integer d;
	private Integer e;
	private Integer f;
	private Integer g;
	private Integer h;
	private Integer i;
	private Integer j;
	private Integer k;
	private Integer m;
	private Integer n;
	private Integer o;
	private Integer p;
	private Integer q;
	private Integer t;
	private Integer u;
	private Integer v;
	private Integer w;
	private Integer x;
	private Integer z;
	private Integer north;
	private Integer south;
	private Integer west;
	private Integer east;
	private Integer northEast;
	private Integer northWest;
	private Integer southEast;
	private Integer southWest;
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
