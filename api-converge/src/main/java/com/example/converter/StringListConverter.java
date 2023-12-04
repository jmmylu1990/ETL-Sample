package com.example.converter;

import org.springframework.util.ObjectUtils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Converter
public class StringListConverter implements AttributeConverter<List<String>, String> {

    @Override
    public String convertToDatabaseColumn(List<String> stringList) {
        return String.join(",", stringList);
    }

    @Override
    public List<String> convertToEntityAttribute(String string) {
        return ObjectUtils.isEmpty(string) ? new ArrayList<>() : Arrays.asList(string.split(","));
    }
}