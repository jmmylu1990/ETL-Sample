package com.example.model.dto.source.traffic.etag;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.example.model.dto.etl.DetailModel;
import com.example.utils.annotation.CsvFormat;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public @Data class Flow extends DetailModel implements Serializable {
	private static final long serialVersionUID = 1L;

	private int uuid = super.getUuid();
	private String eTagPairID;
	private String authorityCode;
	private Integer vehicleType;
	private Integer travelTime;
	private Integer standardDeviation;
	private Integer spaceMeanSpeed;
	private Integer vehicleCount;
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "GMT+8")
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date srcUpdateTime;
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "GMT+8")
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updateTime;
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "GMT+8")
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date infoTime;
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "GMT+8")
	@CsvFormat(pattern = "yyyy-MM-dd")
	private Date infoDate;

}
