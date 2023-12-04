package com.example.model.enums;

import java.util.stream.Stream;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum StateEnum {

	DISABLED(0), ENABLED(1);
	
	private int code;
	
	public static StateEnum fromCode(int code) {
		return Stream.of(StateEnum.values())
			.filter(s -> s.code == code)
			.findAny()
			.orElse(DISABLED);
	}
	
	public static StateEnum toggle(int code) {
		int nextCode = (code + 1) % 2;
		return Stream.of(StateEnum.values())
			.filter(s -> s.code == nextCode)
			.findAny()
			.orElse(DISABLED);
	}
	
	public static StateEnum toggle(StateEnum stateEnum) {
		return toggle(stateEnum.getCode());
	}
}
