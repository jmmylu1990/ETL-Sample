package com.example.model.dto.source.tomtom;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.example.utils.DateUtils;
import com.example.utils.annotation.AssignFrom;
import com.example.utils.annotation.CsvFormat;

import lombok.Data;

public @Data class DynamicInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer sectionID;
	private Integer travelTime;
	private Double travelSpeed;
	private String congestionLevelID = "-99";
	private String congestionLevel = "-99";
	private Integer hasHistorical = 0;
	private Integer hasVD = 0;
	private Integer hasAVI = 0;
	private Integer hasETAG = 0;
	private Integer hasGVP = 1;
	private Integer hasCVP = 0;
	private Integer hasOthers = 0;
	@AssignFrom(name = "updateTime")
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "GMT+8")
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date dataCollectTime;
	@AssignFrom(name = "updateTime")
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "GMT+8")
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date srcUpdateTime;
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "GMT+8")
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updateTime;
	@AssignFrom(name = "updateTime")
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "GMT+8")
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date infoTime;
	@AssignFrom(name = "updateTime")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	@CsvFormat(pattern = "yyyy-MM-dd")
	private Date infoDate;

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(sectionID).append("\t").append(travelTime).append("\t").append(travelSpeed).append("\t")
				.append(congestionLevelID).append("\t").append(congestionLevel).append("\t").append(hasHistorical)
				.append("\t").append(hasVD).append("\t").append(hasAVI).append("\t").append(hasETAG).append("\t")
				.append(hasGVP).append("\t").append(hasCVP).append("\t").append(hasOthers).append("\t")
				.append(DateUtils.formatDateToStr(dataCollectTime)).append("\t")
				.append(DateUtils.formatDateToStr(srcUpdateTime)).append("\t")
				.append(DateUtils.formatDateToStr(updateTime)).append("\t").append(DateUtils.formatDateToStr(infoTime))
				.append("\t").append(DateUtils.formatDateToStr(DateUtils.DASHED_DATE_FORMAT, infoDate));

		return builder.toString();
	}

}
