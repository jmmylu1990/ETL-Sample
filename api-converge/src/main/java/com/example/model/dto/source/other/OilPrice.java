package com.example.model.dto.source.other;

import java.io.Serializable;
import java.util.Date;

import com.example.utils.annotation.AssignFrom;
import com.example.utils.annotation.CsvFormat;

import lombok.Data;

public @Data class OilPrice implements Serializable {
	
	private static final long serialVersionUID = 1L;
   
	private String productNo;	
	private String productName;	
	private String productPackage;	
	private String salesTarget;	
	private String tradingLocation;
	private String salesUnit;	
	private Double referencePrice;	
	private String businessTax;	
	private String commodityTax;	
	private String remark;	
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date srcUpdateTime;
    @CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updateTime;
	@AssignFrom(name = "srcUpdateTime")
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date infoTime;
	@AssignFrom(name = "infoTime")
	@CsvFormat(pattern = "yyyy-MM-dd")
	private Date infoDate;
}
