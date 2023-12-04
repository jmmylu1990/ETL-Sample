package com.example.model.dto.source.cwb.observation;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.JsonNode;
import com.example.model.dto.source.cwb.observation.wrap.Element;
import com.example.model.dto.source.cwb.observation.wrap.Paramter;
import com.example.utils.DateUtils;
import com.example.utils.annotation.AssignFrom;
import com.example.utils.annotation.CsvFormat;

import lombok.Data;

public @Data class WeatherObservation implements Serializable {

	private static final long serialVersionUID = 1L;
	private String citySN;
	private String cityName;
	private String townSN;
	private String townName;
	private String stationID;
	private String locationName;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date obsTime;
	private Double temp;
	private Double humd;
	private Double rainfall;
	private Double dTX;
	private Double dTN;
	private Double lon;
	private Double lat;
	@AssignFrom(name = "obsTime")
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date srcUpdateTime;
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updateTime;
	@AssignFrom(name = "obsTime")
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date infoTime;
	@CsvFormat(pattern = "yyyy-MM-dd")
	@AssignFrom(name = "infoTime")
	private Date infoDate;
	private List<Element> weatherElements;
	private List<Paramter> parameters;
	
	@JsonSetter(value = "time")
	public void setObsTime(JsonNode jsonNode) {
		this.obsTime = DateUtils.parseStrToDate(jsonNode.path("obsTime").asText());
	}
	
	@JsonProperty(value = "weatherElement")
	public void setWeatherElements(List<Element> weatherElements) {
		this.weatherElements = weatherElements;
		for (Element element : weatherElements) {
			String elementValue = element.getElementValue();
			//if (!elementValue.matches("\\-?\\d+\\.?\\d+")) continue;
			
			if (element.getElementName().equals("TEMP")) {
				temp = Double.parseDouble(elementValue);
			} else if (element.getElementName().equals("HUMD")) {
				humd = Double.parseDouble(elementValue);
			} else if (element.getElementName().equals("H_24R")) {
				rainfall = Double.parseDouble(elementValue);
			} else if (element.getElementName().equals("D_TX")) {
				dTX = Double.parseDouble(elementValue);
			} else if (element.getElementName().equals("D_TN")) {
				dTN = Double.parseDouble(elementValue);
			}
		}
	}
	
	@JsonProperty(value = "parameter")
	public void setParameters(List<Paramter> parameters) {
		this.parameters = parameters;
		for (Paramter paramter : parameters) {
			String parameterValue = paramter.getParameterValue();
			if (paramter.getParameterName().equals("CITY")) {
				cityName = parameterValue;
			}
			if (paramter.getParameterName().equals("CITY_SN")) {
				citySN = parameterValue;
			}
			if (paramter.getParameterName().equals("TOWN")) {
				townName = parameterValue;
			}
			if (paramter.getParameterName().equals("TOWN_SN")) {
				townSN = parameterValue;
			}
		}
	}

	
}
