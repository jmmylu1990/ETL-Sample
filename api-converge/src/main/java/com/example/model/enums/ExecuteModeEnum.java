package com.example.model.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ExecuteModeEnum {

	COMMON_MODE(0, "例行性狀態"), RETRY_MODE(1, "重做模式"), RESTORE_MODE(2, "回補模式");
	
	private int code;
	
	private String desc;
}
