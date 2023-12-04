package com.example.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.example.utils.jackson.DuplicateToArrayJsonNodeDeserializer;

public class XmlUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(XmlUtils.class);
	private static XmlMapper xmlMapper;
	
	static {
		initialMapper();
	}
	
	private XmlUtils() {
	}

	public static XmlMapper initialMapper() {
		xmlMapper = new XmlMapper();
		xmlMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
				.configure(Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
				.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
				.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
				.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
				.registerModule(new SimpleModule().addDeserializer(
						JsonNode.class, 
						new DuplicateToArrayJsonNodeDeserializer()
				));
		
		return xmlMapper;
	}
	
	public static XmlMapper getMapper() {
		return Objects.nonNull(xmlMapper) ? xmlMapper : initialMapper();
	}

	public static <E> E toBean(String xmlContent, Class<E> beanClass) {
		try {
			return getMapper().readValue(xmlContent, beanClass);
		} catch (Exception e) {
			LOGGER.error("Error content: {}", xmlContent);
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}
	
	public static <E> List<E> toBeanList(String xmlContent, Class<E> beanClass) {
		try {
			CollectionType typeReference = TypeFactory.defaultInstance().constructCollectionType(List.class, beanClass);

			return getMapper().readValue(xmlContent, typeReference);
		} catch (Exception e) {
			LOGGER.error("Error content: {}", xmlContent);
			LOGGER.error(e.getMessage(), e);
		}
		return new ArrayList<>();
	}
}
