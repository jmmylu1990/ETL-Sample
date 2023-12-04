package com.example.model.dto.source.ptx.bus.ref;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.example.model.dto.source.ptx.ubike.wrap.NameType;
import com.example.utils.annotation.CsvIgnore;

import lombok.Data;

public @Data class BusSubRoute implements Serializable {

	private static final long serialVersionUID = 1L;

	private String subRouteUID;

	private String subRouteID;

	@CsvIgnore
	private List<String> operatorIDs;

	private NameType subRouteName;

	private String headsign;

	private String headsignEn;

	private Integer direction;

	@JsonFormat(pattern = "HHmm", timezone = "GMT+8")
	private Date firstBusTime;

	@JsonFormat(pattern = "HHmm", timezone = "GMT+8")
	private Date lastBusTime;

	@JsonFormat(pattern = "HHmm", timezone = "GMT+8")
	private Date holidayFastBusTime;

	@JsonFormat(pattern = "HHmm", timezone = "GMT+8")
	private Date holidayLastBusTime;
}
