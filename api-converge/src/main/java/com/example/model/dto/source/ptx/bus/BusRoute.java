package com.example.model.dto.source.ptx.bus;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.example.model.dto.source.ptx.basic.Operator;
import com.example.model.dto.source.ptx.bus.ref.BusSubRoute;
import com.example.model.dto.source.ptx.ubike.wrap.NameType;
import com.example.utils.StringTools;
import com.example.utils.annotation.AssignFrom;
import com.example.utils.annotation.CsvFlatten;
import com.example.utils.annotation.CsvFormat;
import com.example.utils.annotation.CsvIgnore;

import lombok.Data;

public @Data class BusRoute implements Serializable {

	private static final long serialVersionUID = 1L;

	private String routeUID;
    
    private String routeID;
    
    private Boolean hasSubRoutes;
    
    @CsvFlatten(delimiter = StringTools.TAB)
    private List<Operator> operators;
    
    private String authorityID;
    
    private String providerID;
    
    @CsvFlatten(delimiter = StringTools.TAB)
    private List<BusSubRoute> subRoutes;
    
    private Integer busRouteType;
    
    private NameType routeName;
    
    private String departureStopNameZh;
    
    private String departureStopNameEn;
    
    private String destinationStopNameZh;
    
    private String destinationStopNameEn;
    
    private String ticketPriceDescriptionZh;
    
    private String ticketPriceDescriptionEn;
    
    private String fareBufferZoneDescriptionZh;
    
    private String fareBufferZoneDescriptionEn;
    
    private String routeMapImageUrl;
    
    private String city;
    
    private String cityCode;
    
    @JsonProperty("updateTime")
    @CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date srcUpdateTime;

    @JsonIgnore
    @CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
    
    @JsonIgnore
    @AssignFrom(name = "srcUpdateTime")
    @CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date infoTime;
    
    @JsonIgnore
    @AssignFrom(name = "infoTime")
    @CsvFormat(pattern = "yyyy-MM-dd")
    private Date infoDate;
	
    @CsvIgnore
    private Integer versionId;
}
