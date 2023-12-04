package com.example.model.dto.source.traffic.etag;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.example.utils.annotation.CsvFormat;
import com.example.utils.annotation.CsvIgnore;

import lombok.Data;

public @Data class ETagPairLive implements Serializable {
	private static final long serialVersionUID = 1L;

	private String eTagPairID;
	private String authorityCode;
	private String subAuthorityCode;
	private Integer startETagStatus;
	private Integer endETagStatus;
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "GMT+8")
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date startTime;
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "GMT+8")
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date endTime;
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "GMT+8")
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date dataCollectTime;
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
	@CsvIgnore
	private List<Flow> flows;


}
