package com.example.factory;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.example.strategy.SQLCallerStrategy;

@Component
public class SQLCallerStrategyFactory {

	@Autowired
    private Map<String, SQLCallerStrategy> sqlCallerStrategyMap;

	public SQLCallerStrategy getObject(String strategyName) {
		return this.sqlCallerStrategyMap.get(strategyName);
	}
}
