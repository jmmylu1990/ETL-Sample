package com.example.model.dto.etl;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.example.utils.annotation.CsvIgnore;

public class DetailModel {

	@CsvIgnore
	@JsonIgnore
	private int uuid;

	public int getUuid() {
		return uuid;
	}

	public void setUuid(int uuid) {
		this.uuid = uuid;
	}
	
	
}
