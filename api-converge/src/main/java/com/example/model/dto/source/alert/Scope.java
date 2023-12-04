package com.example.model.dto.source.alert;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

public @Data class Scope implements Serializable{
	private static final long serialVersionUID = 1L;
	
	AlertKaoNetwork network;
	List<AlertKaoStations> stations;
	List<AlertKaoLines> lines;
	List<AlertKaoRoutes> routes;
    List<AlertKaoTrains> trains;
    List<AlertKaoLineSections> lineSections;
}
