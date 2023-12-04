package com.example.model.dto.source.ptx.bus;

import java.io.Serializable;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.example.model.dto.etl.DetailModel;
import com.example.model.dto.source.ptx.bus.ref.NameTypeOfRoute;
import com.example.utils.annotation.AssignFrom;
import com.example.utils.annotation.CsvFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
@EqualsAndHashCode(callSuper = true)
public @Data class BusAlertKaoRoute extends DetailModel implements Serializable{

	private static final long serialVersionUID = 1L;

	private String alertID;
	private String routeID;	
	private NameTypeOfRoute routeName;	
	private Integer direction;	
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "GMT+8")
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
