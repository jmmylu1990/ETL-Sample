package com.example.strategy;

import com.example.exception.ResourceException;
import com.example.model.dto.etl.ExtractResult;
import com.example.model.dto.etl.ResourceInfo;

public interface SQLCallerStrategy {

	public ExtractResult executeSP(ResourceInfo resourceInfo) throws ResourceException;

}
