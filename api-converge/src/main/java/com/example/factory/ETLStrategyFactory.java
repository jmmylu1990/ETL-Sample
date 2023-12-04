package com.example.factory;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.strategy.ETLStrategy;

@Component
public class ETLStrategyFactory {

	@Autowired
    private Map<String, ETLStrategy> etlStrategyMap;

	public ETLStrategy getObject(String strategyName) {
		return this.etlStrategyMap.get(strategyName);
	}
}
