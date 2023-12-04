package com.example.model.dto.source.ntpc;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.example.utils.annotation.AssignFrom;
import com.example.utils.annotation.CsvFormat;

import lombok.Data;

/** CodeGen DTO for `HolidayListDetail` **/
public @Data class HolidayListDetail implements Serializable {

	private static final long serialVersionUID = 1L;

	private String yearMonth;	

	private String lastYearMonth;	
	
	@JsonProperty("date")
	@JsonFormat(pattern = "yyyy/M/d", timezone = "GMT+8")
	@CsvFormat(pattern = "yyyy/MM/dd")
	private Date dataDate;

	private String weekday;

	@JsonProperty("name")
	private String dataName;

	private String isHoliday;

	private String holidayCategory;

	private String type01 = "N";

	private String type02 = "N";

	private String type03 = "N";

	private String type04 = "N";

	private String type05 = "N";

	private String type06 = "N";

	private String type07 = "N";

	private String type08 = "N";

	private String type09 = "N";

	private String type10 = "N";
	
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date srcUpdateTime;
	
	@AssignFrom(name = "srcUpdateTime")
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updateTime;
	
	@AssignFrom(name = "dataDate")
	@CsvFormat(pattern = "yyyy-MM-dd HH:05:ss")
	private Date infoTime;
	
	@AssignFrom(name = "infoTime")
	@CsvFormat(pattern = "yyyy-MM-dd")
	private Date infoDate;

}