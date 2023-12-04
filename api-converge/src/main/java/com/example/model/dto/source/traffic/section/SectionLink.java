package com.example.model.dto.source.traffic.section;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.JsonNode;
import com.example.utils.annotation.AssignFrom;
import com.example.utils.annotation.CsvFlatten;
import com.example.utils.annotation.CsvFormat;
import com.example.utils.enums.FlatModeEnum;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(of = { "sectionID", "authorityCode", "subAuthorityCode" })
public @Data class SectionLink implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private String sectionID;
	private String authorityCode;
	private String subAuthorityCode;
	private String startLinkID;
	private String endLinkID;
	@CsvFlatten(mode = FlatModeEnum.HORIZONTAL)
	private List<String> linkIDs;
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
	
	@JsonSetter("LinkIDs")
	public void setLinkIDs(JsonNode node) {
		this.linkIDs = StreamSupport.stream(node.spliterator(), false)
			.map(item -> item.get("LinkID").asText())
			.collect(Collectors.toList());	
	}
	
}
