package com.example.model.dto.source.tdcs;

import java.util.Date;
import java.util.List;

import com.example.utils.annotation.AssignFrom;
import com.example.utils.annotation.CsvFormat;

import lombok.Data;

public @Data class  NfbEtagM06Master implements java.io.Serializable{

	private static final long serialVersionUID = 1L;
	private String etagM06MasterID;
	private String vehicleType;
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date detectionTimeO;
	private String gantryIDO;
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date detectionTimeD;
	private String gantryIDD;
	private Double tripLength;
	private String tripEnd;
	private String authority;
	private List<NfbEtagM06Detail> nfbEtagM06Details;
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
