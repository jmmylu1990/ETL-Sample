package com.example.model.dto.etl;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public @Data class ImportResult implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private long importCount;

	private Exception exception;
}
