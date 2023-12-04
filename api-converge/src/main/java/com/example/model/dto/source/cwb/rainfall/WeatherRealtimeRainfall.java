package com.example.model.dto.source.cwb.rainfall;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.JsonNode;
import com.example.model.dto.source.cwb.wrap.Element;
import com.example.model.dto.source.cwb.wrap.Paramter;
import com.example.utils.DateUtils;
import com.example.utils.annotation.AssignFrom;
import com.example.utils.annotation.CsvFormat;
import lombok.Data;

public @Data class WeatherRealtimeRainfall implements Serializable {
	private static final long serialVersionUID = 1L;
	private String stationId;
	private String locationName;
	private Double lat;
	private Double lon;
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date obsTime;
	private Double elev;
	private Double rain;
	private Double min10;
	private Double hour3;
	private Double hour6;
	private Double hour12;
	private Double hour24;
	private Double now;
	private Double latest2days;
	private Double latest3days;
	private String city;
	private String citySn;
	private String town;
	private String townSn;
	private String attribute;
	@JsonProperty("sent")
	@CsvFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private Date srcUpdateTime;
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updateTime;
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@AssignFrom(name = "obsTime")
	private Date infoTime;
	@CsvFormat(pattern = "yyyy-MM-dd")
	@AssignFrom(name = "obsTime")
	private Date infoDate;
	private List<Element> weatherElements;
	private List<Paramter> parameters;

	@JsonSetter(value = "time")
	public void setObsTime(JsonNode jsonNode) {
		if (jsonNode.path("time").asText() != null) {
			this.obsTime = DateUtils.parseStrToDate(jsonNode.path("obsTime").asText());
			//過濾後的時,該"time"屬性jsonNode只有值
			if (DateUtils.parseStrToDate(jsonNode.path("obsTime").asText()) == null) {
				this.obsTime = DateUtils.parseStrToDate(jsonNode.asText());
			}

		}

	}

	@JsonProperty(value = "weatherElement")
	public void setWeatherElements(List<Element> weatherElements) {
		this.weatherElements = weatherElements;
		for (Element element : weatherElements) {

			String elementValue = element.getElementValue();
			
			if (!elementValue.matches("\\-?\\d+\\.?\\d+"))
				continue;

			if (element.getElementName().equals("ELEV")) {
				elev = Double.parseDouble(elementValue);
			} else if (element.getElementName().equals("RAIN")) {
				rain = Double.parseDouble(elementValue);
			} else if (element.getElementName().equals("MIN_10")) {
				min10 = Double.parseDouble(elementValue);
			} else if (element.getElementName().equals("HOUR_3")) {
				hour3 = Double.parseDouble(elementValue);
			} else if (element.getElementName().equals("HOUR_6")) {
				hour6 = Double.parseDouble(elementValue);
			} else if (element.getElementName().equals("HOUR_12")) {
				hour12 = Double.parseDouble(elementValue);
			} else if (element.getElementName().equals("HOUR_24")) {
				hour24 = Double.parseDouble(elementValue);
			} else if (element.getElementName().equals("NOW")) {
				now = Double.parseDouble(elementValue);
			} else if (element.getElementName().equals("latest_2days")) {
				latest2days = Double.parseDouble(elementValue);
			} else if (element.getElementName().equals("latest_3days")) {
				latest3days = Double.parseDouble(elementValue);
			}
		}
	}

	@JsonProperty(value = "parameter")
	public void setParameters(List<Paramter> parameters) {
		this.parameters = parameters;
		for (Paramter paramter : parameters) {
			String parameterValue = paramter.getParameterValue();
			if (paramter.getParameterName().equals("CITY")) {
				city = parameterValue;
			}
			if (paramter.getParameterName().equals("CITY_SN")) {
				citySn = parameterValue;
			}
			if (paramter.getParameterName().equals("TOWN")) {
				town = parameterValue;
			}
			if (paramter.getParameterName().equals("TOWN_SN")) {
				townSn = parameterValue;
			}
			if (paramter.getParameterName().equals("ATTRIBUTE")) {
				attribute = parameterValue;
			}
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(stationId).append("\t").append(locationName).append("\t").append(lat).append("\t").append(lon)
				.append("\t").append(DateUtils.formatDateToStr(obsTime)).append("\t").append(elev).append("\t")
				.append(rain).append("\t").append(min10).append("\t").append(hour3).append("\t").append(hour6)
				.append("\t").append(hour12).append("\t").append(hour24).append("\t").append(now).append("\t")
				.append(latest2days).append("\t").append(latest3days).append("\t").append(city).append("\t")
				.append(citySn).append("\t").append(town).append("\t").append(townSn).append("\t").append(attribute)
				.append("\t").append(DateUtils.formatDateToStr(srcUpdateTime)).append("\t")
				.append(DateUtils.formatDateToStr(updateTime)).append("\t").append(DateUtils.formatDateToStr(infoTime))
				.append("\t").append(DateUtils.formatDateToStr(DateUtils.DASHED_DATE_FORMAT, infoDate));
		return builder.toString();
	}

}
