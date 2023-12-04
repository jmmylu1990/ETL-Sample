package com.example.model.dto.source.iot.device;

import java.io.Serializable;
import java.util.Date;
import com.example.utils.annotation.AssignFrom;
import com.example.utils.annotation.CsvFormat;
import lombok.Data;

public @Data class HinetHourlyPeopleFlow implements Serializable {
	private static final long serialVersionUID = 1L;

	private String id;
	private String deviceID;
	//@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date dataTime;
	private Integer southPeopleFlowCounts;
	private Integer southDataCounts;
	private Integer northPeopleFlow_counts;
	private Integer northDataCounts;
	@AssignFrom(name = "dataTime")
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
