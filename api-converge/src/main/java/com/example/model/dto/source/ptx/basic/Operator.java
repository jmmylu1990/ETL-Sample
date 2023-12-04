package com.example.model.dto.source.ptx.basic;

import java.io.Serializable;

import com.example.model.dto.source.ptx.ubike.wrap.NameType;
import com.example.utils.annotation.CsvIgnore;

import lombok.Data;

public @Data class Operator implements Serializable {

	private static final long serialVersionUID = 1L;

	private String operatorID;

    private NameType operatorName;

    @CsvIgnore
    private String operatorCode;

    @CsvIgnore
    private String operatorNo;
}
