package com.example.model.dto.etl;

import java.io.File;
import java.util.Date;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ExtractResult {

	private List<File> resources;
	
	private Date srcUpdateTime;

	private Date updateTime;

}
