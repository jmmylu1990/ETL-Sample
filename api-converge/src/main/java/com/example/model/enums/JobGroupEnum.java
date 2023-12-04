package com.example.model.enums;

import java.util.stream.Stream;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum JobGroupEnum {

	ETL_GROUP("ETL-Group"),
	SQL_CALLER_GROUP("SQL-Caller-Group"),
	SHELL_CALLER_GROUP("Shell-Caller-Group"),
	API_TRIGGER_GROUP("API-Trigger-Group");
	
	private String name;

	public static JobGroupEnum fromName(String name) {
		return Stream.of(JobGroupEnum.values())
				.filter(j -> j.getName().equals(name))
				.findAny()
				.orElse(null);
	}

}
