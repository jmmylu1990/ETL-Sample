package com.example.model.dto.source.traffic.etag;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.example.model.dto.source.cwb.observation.wrap.Element;
import com.example.utils.annotation.AssignFrom;
import com.example.utils.annotation.CsvFormat;
import com.example.utils.annotation.CsvIgnore;

import lombok.Data;

public @Data class ETagPair implements Serializable {

	private static final long serialVersionUID = 1L;

	private String eTagPairID;
	private String authorityCode;
	private String subAuthorityCode;
	private String startETagGantryID;
	private String endETagGantryID;
	private String description;
	private Double distance;
	private String startlinkID;
	private String endlinkID;

	private String linkIDList;
	private String sectionID;
	private String geometry;
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "GMT+8")
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date srcUpdateTime;
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonIgnore
	private Date updateTime;
	@AssignFrom(name = "srcUpdateTime")
	@CsvFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date infoTime;
	@AssignFrom(name = "infoTime")
	@CsvFormat(pattern = "yyyy-MM-dd")
	private Date infoDate;
//
//	@CsvIgnore
//	private List<String> linkIDs;

	@JsonProperty(value = "LinkIDs")
	public void setLinkIDs(JsonNode jsonNode) {

		List<String> list = new ArrayList<String>();
		for (JsonNode linkID : jsonNode) {
			
			list.add(linkID.get("LinkID").asText());
		}
		this.linkIDList = list.toString().replace("[", "").replace("]", "");
	}

}
