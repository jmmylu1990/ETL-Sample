package com.example.model.enums;

import java.util.stream.Stream;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DbSourceEnum {

	MY_SQL("mysql"), MS_SQL("mssql"), ORACLE("oracle"), IMPALA("impala"), JSON("json"), XML("xml");

	private String name;

	public static DbSourceEnum fromName(String name) {
		return Stream.of(DbSourceEnum.values())
			.filter(dbSource -> dbSource.getName().equals(name))
			.findFirst()
			.orElseThrow(IllegalArgumentException::new);
	}
}
