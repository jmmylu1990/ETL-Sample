package com.example.model.dto.source.cwb.forecast;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.example.model.dto.source.cwb.forecast.wrap.Element;
import com.example.model.dto.source.cwb.forecast.wrap.ElementValue;
import com.example.model.dto.source.cwb.forecast.wrap.Time;
import com.example.utils.annotation.AssignFrom;
import com.example.utils.annotation.CsvFormat;
import lombok.Data;

public @Data class WeatherForecast implements Serializable {

	private static final long serialVersionUID = 1L;

	private String cityName;
	
	@JsonProperty("locationName")
	private String townName;
	
	private String geoCode;
	
	private Double lat;
	
	private Double lon;
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date startTime;
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date endTime;
	
	private Integer pop12h;
	
	private Integer t;
	
	private Integer rh;
	
	private Integer minCi;
	
	private String ws1;
	
	private String ws2;
	
	private Integer maxAt;
	
	private Integer minAt;
	
	private String wx1;
	
	private Integer wx2;
	
	private Integer maxCi;
	
	private Integer maxT;
	
	private Integer minT;
	
	private String uvi1;
	
	private String uvi2;
	
	private String weatherDescription;
	
	private String wd;
	
	private Integer td;
	
	@AssignFrom(name = "updateTime")
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date srcUpdateTime;
	
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updateTime;
	
	@AssignFrom(name = "endTime")
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date infoTime;
	
	@AssignFrom(name = "infoTime")
	@CsvFormat(pattern = "yyyy-MM-dd")
	private Date infoDate;
	
	private List<Element> weatherElements;

	@JsonProperty("weatherElement")
	public void setWeatherElements(List<Element> weatherElements) {
		this.weatherElements = weatherElements;
		for (Element element : weatherElements) {
			String elementName = element.getElementName();
			List<Time> times = element.getTimes();
			Time firstTime = times.isEmpty() ? new Time() : times.get(0); // Since there will use time filter, so that result will be single
			List<ElementValue> elementValues = firstTime.getElementValues();
			ElementValue firstElementValue = elementValues.isEmpty() ? new ElementValue() : elementValues.get(0); // Generally it usually be single value
			
			if (elementName.equals("PoP12h")) {
				this.pop12h = Integer.parseInt(firstElementValue.getValue());
				this.startTime = firstTime.getStartTime();
				this.endTime = firstTime.getEndTime();
			} else if (elementName.equals("T")) {
				this.t = Integer.parseInt(firstElementValue.getValue());
			} else if (elementName.equals("RH")) {
				this.rh = Integer.parseInt(firstElementValue.getValue());
			} else if (elementName.equals("MinCI")) {
				this.minCi = Integer.parseInt(firstElementValue.getValue());
			} else if (elementName.equals("WS")) {
				this.ws1 = firstElementValue.getValue();
				this.ws2 = elementValues.size() > 1 ? elementValues.get(1).getValue() : null;
			} else if (elementName.equals("MaxAT")) {
				this.maxAt = Integer.parseInt(firstElementValue.getValue());
			} else if (elementName.equals("MinAT")) {
				this.minAt = Integer.parseInt(firstElementValue.getValue());
			} else if (elementName.equals("WX")||elementName.equals("Wx")) {
				this.wx1 = firstElementValue.getValue();
				this.wx2 = elementValues.size() > 1 ? Integer.parseInt(elementValues.get(1).getValue()) : null;
			} else if (elementName.equals("MaxCI")) {
				this.maxCi = Integer.parseInt(firstElementValue.getValue());
			} else if (elementName.equals("MaxT")) {
				this.maxT = Integer.parseInt(firstElementValue.getValue());
			} else if (elementName.equals("MinT")) {
				this.minT = Integer.parseInt(firstElementValue.getValue());
			} else if (elementName.equals("UVI")) {
				this.uvi1 = firstElementValue.getValue();
				this.uvi2 = elementValues.size() > 1 ? elementValues.get(1).getValue() : null;
			} else if (elementName.equals("WeatherDescription")) {
				this.weatherDescription = firstElementValue.getValue();
			} else if (elementName.equals("WD")) {
				this.wd = firstElementValue.getValue();
			} else if (elementName.equals("Td")) {
				this.td = Integer.parseInt(firstElementValue.getValue());
			}
		}
	}

}