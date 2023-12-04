package com.example.model.dto.source.traffic.live;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.example.utils.annotation.AssignFrom;
import com.example.utils.annotation.CsvFormat;

import lombok.Data;

public @Data class VDFiveMinForNFB implements Serializable {
	private static final long serialVersionUID = 1L;

	private String vdID;
	private String status;
	private String vsrDir;
	private String vsrID;
	private Double speed;
	private Double laneoccupy;
	private Integer sVolume;
	private Integer tVolume;
	private Integer lVolume;
	private Integer mVolume;
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date dataCollectTime;
	@CsvFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private Date srcUpdateTime;
	@CsvFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private Date updateTime;
	@AssignFrom(name = "dataCollectTime")
	@CsvFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private Date infoTime;
	@AssignFrom(name = "dataCollectTime")
	@CsvFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private Date infoDate;
}
