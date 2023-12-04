package com.example.model.dto.source.traffic.device;

import java.io.Serializable;
import java.util.Date;

import com.example.model.dto.etl.DetailModel;
import com.example.utils.annotation.AssignFrom;
import com.example.utils.annotation.CsvFormat;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public @Data class VDDetectionlink extends DetailModel implements Serializable {

	private static final long serialVersionUID = 1L;
	private int uuid = super.getUuid();
	private String vdID;
	private String authorityCode;
	private String linkID;
	private String bearing;
	private String roadDirection;
	private Integer laneNum;
	private Integer actualLaneNum;
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
