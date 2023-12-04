package com.example.model.dto;

import java.util.List;

import com.example.model.enums.ETLResultEnum;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotifyArgument {

	private String jobName;
	
	private String resourceUrl;

	private int processIndex;
	
	private long resourceCount;
	
	private int errorAccumulation;
	
	private ETLResultEnum apistatus;
	
	private Exception exception;

	private List<String> notifyMails;
}
