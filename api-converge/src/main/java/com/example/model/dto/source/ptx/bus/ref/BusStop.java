package com.example.model.dto.source.ptx.bus.ref;

import java.io.Serializable;

import com.example.model.dto.source.ptx.basic.PointTypeForBus;
import com.example.model.dto.source.ptx.ubike.wrap.NameType;
import lombok.Data;

public @Data class BusStop implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String stopUID;
	
    private String stopID;
    
    private NameType stopName;
    
    private Integer stopBoarding;
    
    private Integer stopSequence;
    
    private PointTypeForBus stopPosition;
    
    private String stationID;

    private String stationGroupID;
    
    private String locationCityCode;
}
