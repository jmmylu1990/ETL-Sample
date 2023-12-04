package com.example.model.dto.source.traffic.live.wrap;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.example.utils.annotation.CsvFormat;

import lombok.Data;

public @Data class Info implements Serializable{
	private static final long serialVersionUID = 1L;
	@JsonProperty("vdid")
	private String vdID;
	private String status;
	@JsonProperty("datacollecttime")
	@JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
	private Date datacollectTime;
	@JsonProperty("lane")
	private List<Lane> lanes;
}
