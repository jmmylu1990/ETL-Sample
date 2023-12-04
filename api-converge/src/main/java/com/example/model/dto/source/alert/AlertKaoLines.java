package com.example.model.dto.source.alert;

import java.io.Serializable;
import java.util.Date;

import com.example.model.dto.etl.DetailModel;
import com.example.utils.annotation.CsvFormat;

import lombok.Data;

public @Data class AlertKaoLines extends DetailModel implements Serializable {

	private static final long serialVersionUID = 1L;

	private String alertID;
	private String lineID;
	private String lineName;
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date srcUpdateTime;
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updateTime;
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date infoTime;
	@CsvFormat(pattern = "yyyy-MM-dd")
	private Date infoDate;

}
