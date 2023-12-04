package com.example.utils;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

public @Data class FileStatus implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private Date accessTime;
	
	private Long blockSize;
	
	private Integer childrenNum;
	
	private Long fileId;
	
	private String group;
	
	private Integer length;
	
	private Date modificationTime;
	
	private String owner; 

	private String pathSuffix; 
	
	private String permission;
	
	private Integer replication;
	
	private Integer storagePolicy;

	private String type;
}
