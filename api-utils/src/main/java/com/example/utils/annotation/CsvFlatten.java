package com.example.utils.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.example.utils.StringTools;
import com.example.utils.enums.FlatModeEnum;


@Documented
@Target( { ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface CsvFlatten {

	FlatModeEnum mode() default FlatModeEnum.VERTICAL;

	String delimiter() default StringTools.COMMA;
}