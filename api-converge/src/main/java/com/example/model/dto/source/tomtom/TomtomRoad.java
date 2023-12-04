package com.example.model.dto.source.tomtom;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.example.utils.DateUtils;

import lombok.Data;

public @Data class TomtomRoad implements Serializable{

	private static final long serialVersionUID = 1L;

	private Integer id;
	private String roadID;
	private String roadName;
	private String stationO;
	private String stationD;
	private Double oLat;
	private Double oLon;
	private Double dLat;
	private Double dLon;
	private Double mLat;
	private Double mLon;
    private String remark;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "GMT+8")
    private Date updateDate;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss",timezone = "GMT+8")
    private Date createDate;
    
    public String createPointStr(Double lat, Double lon) {
    	return Objects.nonNull(lat) && Objects.nonNull(lon) ? String.format("%f,%f", lat, lon) : null;
    }
    
    public String getStartPointStr() {
    	return this.createPointStr(oLat, oLon);
    }

    public String getMiddlePointStr() {
    	return this.createPointStr(mLat, mLon);
    }
    
    public String getEndPointStr() {
    	return this.createPointStr(dLat, dLon);
    }
    
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(id).append("\t").append(roadID).append("\t").append(roadName).append("\t").append(stationO)
				.append("\t").append(stationD).append("\t").append(oLat).append("\t").append(oLon).append("\t").append(dLat)
				.append("\t").append(dLon).append("\t").append(mLat).append("\t").append(mLon).append("\t").append(remark)
				.append("\t").append(DateUtils.formatDateToStr(updateDate)).append("\t").append(DateUtils.formatDateToStr(createDate));
		return builder.toString();
	}

	
	
}
