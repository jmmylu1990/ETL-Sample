package com.example.model.dto.source.traffic.section.wrap;

import java.io.Serializable;

import lombok.Data;

public @Data class DataSources implements Serializable{
	private static final long serialVersionUID = 1L;
	private Integer hasHistorical;
	private Integer hasVD;
	private Integer hasAVI;
	private Integer hasETAG;
	private Integer hasGVP;
	private Integer hasCVP;
	private Integer hasOthers;
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(hasHistorical).append("\t").append(hasVD).append("\t").append(hasAVI).append("\t").append(hasETAG)
				.append("\t").append(hasGVP).append("\t").append(hasCVP).append("\t").append(hasOthers);
		return builder.toString();
	}
}
