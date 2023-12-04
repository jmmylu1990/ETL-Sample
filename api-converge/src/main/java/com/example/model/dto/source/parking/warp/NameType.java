package com.example.model.dto.source.parking.warp;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

public @Data class NameType implements Serializable {

	private static final long serialVersionUID = 1L;

	@JsonProperty("Zh_tw")
	private String zhTw;

	private String en;

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(zhTw).append("\t").append(en);
		return builder.toString();
	}
	
	

}
