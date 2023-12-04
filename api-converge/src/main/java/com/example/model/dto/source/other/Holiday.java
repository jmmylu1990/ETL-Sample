package com.example.model.dto.source.other;
import java.io.Serializable;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.example.utils.DateUtils;
import com.example.utils.annotation.AssignFrom;
import com.example.utils.annotation.CsvFormat;

import lombok.Data;


public @Data class Holiday implements Serializable{

	private static final long serialVersionUID = 1L;


	@JsonProperty("date")
	@CsvFormat(pattern = "yyyy/MM/dd")
	private Date dataDate;
    private String weekday;	
    private String data_name;
    private String isHoliday;	
    private String holidayCategory;
    private String type01;
    private String type02;
    private String type03;
    private String type04;
    private String type05;
    private String type06;
    private String type07;
    private String type08;
    private String type09;
    private String type010;
	@CsvFormat(pattern = "yyyy/MM/dd")
	private Date srcUpdateTime;
    @CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updateTime;
	@AssignFrom(name = "srcUpdateTime")
	private Date infoTime;
	@AssignFrom(name = "infoTime")
	private Date infoDate;
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(DateUtils.formatDateToStr("yyyy/MM/dd",dataDate)).append("\t").append(weekday).append("\t").append(data_name).append("\t").append(isHoliday)
				.append("\t").append(holidayCategory).append("\t").append(type01).append("\t").append(type02).append("\t")
				.append(type03).append("\t").append(type04).append("\t").append(type05).append("\t").append(type06)
				.append("\t").append(type07).append("\t").append(type08).append("\t").append(type09).append("\t")
				.append(type010).append("\t").append(DateUtils.formatDateToStr(srcUpdateTime)).append("\t")
				.append(DateUtils.formatDateToStr(updateTime)).append("\t")
				.append(DateUtils.formatDateToStr(infoTime)).append("\t")
				.append(DateUtils.formatDateToStr(DateUtils.DASHED_DATE_FORMAT, infoDate));
		return builder.toString();
	}
	
	
	
}
