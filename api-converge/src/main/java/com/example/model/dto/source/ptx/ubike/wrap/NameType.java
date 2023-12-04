package com.example.model.dto.source.ptx.ubike.wrap;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

public @Data class NameType implements Serializable {

	private static final long serialVersionUID = 1L;

	@JsonProperty("Zh_tw")
	private String zhTw;

	private String en;

}
