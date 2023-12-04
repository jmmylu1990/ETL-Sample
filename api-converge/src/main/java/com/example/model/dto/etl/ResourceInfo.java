package com.example.model.dto.etl;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.BeanUtils;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Builder
@Getter
public class ResourceInfo {

	private String resource;

	private String rootPath;
	
	private String relativePath;

	private Class<?> modelClass;
	
	private String targetTable;
	
	public static class ResourceInfoBuilder {
		
		public ResourceInfoBuilder cloneWith(ResourceInfo otherInfo) {
			try {
				BeanUtils.copyProperties(this, otherInfo);
			} catch (IllegalAccessException | InvocationTargetException e) {
				log.error(e.getMessage(), e);
			}
			return this;
		}
	}
	
}
