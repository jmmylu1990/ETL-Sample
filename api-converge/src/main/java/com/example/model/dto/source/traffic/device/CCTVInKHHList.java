package com.example.model.dto.source.traffic.device;

import java.io.Serializable;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.example.model.dto.source.traffic.device.wrap.Position;
import com.example.utils.annotation.AssignFrom;
import com.example.utils.annotation.CsvFormat;
import lombok.Data;

public @Data class CCTVInKHHList implements Serializable{
	private static final long serialVersionUID = 1L;
	@JsonProperty("id")
	private String cctvID;
	@JsonProperty("link_id")
	private String linkID;
	@JsonProperty("url")
	private String videoStreamURL;
	@JsonProperty("location_type")
	private Integer locationType;
	private Position position;
	@JsonProperty("surveillance_type")
	private Integer surveillanceType;
	@JsonProperty("name")
	private String surveillanceDescription;
	@JsonProperty("road_id")
	private String roadID;
	@JsonProperty("road_name")
	private String roadName;
	@JsonProperty("road_class")
	private Integer roadClass;
	@JsonProperty("road_direction")
	private String roadDirection;
	@JsonProperty("locationImage")
	private String layoutMapURL;
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date srcUpdateTime;
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updateTime;
	@AssignFrom(name = "srcUpdateTime")
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date infoTime;
	@CsvFormat(pattern = "yyyy-MM-dd")
	@AssignFrom(name = "srcUpdateTime")
	private Date infoDate;

}
