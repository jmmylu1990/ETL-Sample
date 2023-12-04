package com.example.model.enums;

import java.util.stream.Stream;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ETLResultEnum {

	SUCCESS(0, "介接正常"),
	RESOURCE_UNAVAILABLE(1, "來源系統斷線"),
	RESOURCE_FORMAT_ERROR(2, "來源格式異常"),
	IMPORT_FAIL(3, "匯入異常"),
	RESOURCE_NOT_UPDATE(4, "來源系統未異動"),
	UNKNOWN_ERROR(5, "未知異常");

	private int code;

	private String desc;
	
	public static ETLResultEnum fromCode(int code) {
		return Stream.of(ETLResultEnum.values())
			.filter(apiStatus -> apiStatus.getCode() == code)
			.findFirst().orElse(ETLResultEnum.UNKNOWN_ERROR);
	}
	
	@Override
	public String toString() {
		return String.valueOf(code);
	}

}
