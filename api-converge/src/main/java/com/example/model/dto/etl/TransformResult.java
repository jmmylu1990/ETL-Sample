package com.example.model.dto.etl;

import java.util.Map;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class TransformResult {
	
	private Map<String, EncapsulationFile> importFileMap;
	
}
