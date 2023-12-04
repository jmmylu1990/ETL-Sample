package com.example.model.dto.source.other;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.example.utils.annotation.AssignFrom;
import com.example.utils.annotation.CsvFormat;

import lombok.Data;

public @Data class TESTUbikeImmediate implements Serializable{
	private static final long serialVersionUID = 1L;

	private String sno;
	private String sna;
	private Integer tot;
	private Integer sbi;
	private String sarea;
	@JsonFormat(pattern = "yyyyMMddHHmmss")
	@CsvFormat(pattern = "yyyyMM-dd HH:mm:ss")
	private Date mday;
	private Double lat;
	private Double lng;
	private String ar;
	private String sareaen;
	private String snaen;
	private String aren;
	private Integer bemp;
	private String act;
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date srcUpdateTime;
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updateTime;
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@AssignFrom(name = "srcUpdateTime")
	private Date infoTime;
	@CsvFormat(pattern = "yyyy-MM-dd")
	@AssignFrom(name = "infoTime")
	private Date infoDate;
	
	
}
