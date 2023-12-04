package com.example.model.dto;

import java.util.Map;

import com.example.model.enums.ExecuteModeEnum;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ExecuteArgument {

	private ExecuteModeEnum mode;
	
	private int retryCount;
	
	private String infoDateStr;
	
	private Map<String, Object> moreArguments;
	
	public static ExecuteArgumentBuilder defaultBuilder() {
		return ExecuteArgument.builder()
				.mode(ExecuteModeEnum.COMMON_MODE);
	}
}
