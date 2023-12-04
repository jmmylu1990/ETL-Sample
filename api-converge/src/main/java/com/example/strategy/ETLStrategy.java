package com.example.strategy;

import java.util.Map;

import com.example.exception.ImportException;
import com.example.exception.ResourceException;
import com.example.exception.ResourceFormatErrorException;
import com.example.model.dto.etl.ExtractResult;
import com.example.model.dto.etl.ImportResult;
import com.example.model.dto.etl.ResourceInfo;
import com.example.model.dto.etl.TransformResult;
import com.example.model.enums.DbSourceEnum;

public interface ETLStrategy {

	public ExtractResult extract(ResourceInfo resourceInfo) throws ResourceException;

	public TransformResult transform(ResourceInfo resourceInfo, ExtractResult extractResult) throws ResourceFormatErrorException;

	public Map<DbSourceEnum, ImportResult> load(TransformResult transformResult, boolean clearFirst, DbSourceEnum... dbSourceEnums) throws ImportException;
	
	public void successCallback();
}
