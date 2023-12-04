package com.example.model.dto.etl;

import java.io.File;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
public @Data class EncapsulationFile implements Serializable {

	private static final long serialVersionUID = 1L;

	private File file;
	
	private long lineCount;
	
}
