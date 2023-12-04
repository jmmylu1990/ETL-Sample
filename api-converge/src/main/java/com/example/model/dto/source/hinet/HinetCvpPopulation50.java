package com.example.model.dto.source.hinet;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.example.utils.annotation.AssignFrom;
import com.example.utils.annotation.CsvFormat;

import lombok.Data;

public @Data class HinetCvpPopulation50 implements Serializable{
	private static final long serialVersionUID = 1L;

	private String apiID;
	private String status;	
	private String msg;	
	@JsonProperty("ev_name")
	private String evName;
	@JsonProperty("yyyymmdd")
	@JsonFormat(pattern = "yyyyMMdd", timezone = "GMT+8")
	@CsvFormat(pattern = "yyyyMMdd")
	private Date dataDate;
	private Integer allcnt;
	private Integer male;
	private Integer female;
	private Integer age19;
	private Integer age29;
	private Integer age39;
	private Integer age49;
	private Integer age59;
	private Integer age60;
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
	@AssignFrom(name = "updateTime")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date srcUpdateTime;	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updateTime;	
	@AssignFrom(name = "updateTime")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date infoTime;	
	@AssignFrom(name = "updateTime")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	@CsvFormat(pattern = "yyyy-MM-dd")
	private Date infoDate;
}
