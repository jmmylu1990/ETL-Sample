package com.example.factory;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.model.enums.DbSourceEnum;
import com.example.strategy.FileImportStrategy;

@Component
public class FileImportStrategyFactory {

	@Autowired
    private Map<String, FileImportStrategy> fileImportStrategyMap;

	public FileImportStrategy getObject(DbSourceEnum dbSourceEnum) {
		String beanName = String.format("%sFileImportStrategy", dbSourceEnum.getName());
		return this.fileImportStrategyMap.get(beanName);
	}
}
