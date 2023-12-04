package com.example.strategy.impl.etl;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.github.houbb.opencc4j.util.ZhConverterUtil;
import com.example.service.interfaces.PolyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.example.dao.mysql.TomtomRequestPointRepository;
import com.example.exception.ResourceException;
import com.example.exception.ResourceFormatErrorException;
import com.example.exception.ResourceNotUpdateException;
import com.example.model.dto.etl.ExtractResult;
import com.example.model.dto.etl.ResourceInfo;
import com.example.model.dto.source.tomtom.TomtomEvent;
import com.example.model.entity.mysql.TomtomRequestPoint;
import com.example.utils.ClassUtils;
import com.example.utils.DateUtils;
import com.example.utils.ETLHelper;
import com.example.utils.JsonUtils;
import com.example.utils.StringTools;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("TomtomAPIStrategy")
public class TomtomAPIStrategy extends GeneralApiETLStrategy {

	@Value("${TEST.api.key}")
	private String key;

	@Autowired
	private TomtomRequestPointRepository tomtomRequestPointRepository;

	@Autowired
	private com.example.service.interfaces.PolyService polyService;

	@Override
	public ExtractResult extract(ResourceInfo resourceInfo) throws ResourceException {
		try {
			// Step 1: Check resource available or not
			Date updateTime = new Date();

			List<TomtomRequestPoint> tomtomRequestPointList = tomtomRequestPointRepository.findAll();
			List<List<TomtomEvent>> totalTomtomEvents = tomtomRequestPointList.stream().map(item -> {
				String url = String.format(resourceInfo.getResource(), item.getLeftUpLat(), item.getLeftUpLon(),
						item.getRightDownLat(), item.getRightDownLon(), key);

				List<TomtomEvent> tomtomEvents = null;
				try {

					String content = JsonUtils.toJsonString(url);
					String contentTrans = ZhConverterUtil.toTraditional(content);
					JsonNode jsonNode = JsonUtils.getMapper().readTree(contentTrans);
					ArrayNode poiNodeList = (ArrayNode) jsonNode.get("tm").get("poi");
					String poiNodeListStr = JsonUtils.getMapper().writeValueAsString(poiNodeList);
					tomtomEvents = JsonUtils.toBeanList(poiNodeListStr, TomtomEvent.class);
					tomtomEvents.forEach(s -> {
						s.setZoneID(item.getSeq());
						s.setLineString(polyService.decode(s.getV()));
					});

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				return tomtomEvents;
			}).collect(Collectors.toList());

			List<TomtomEvent> tomtomEventList = totalTomtomEvents.stream().flatMap(list -> list.stream())
					.collect(Collectors.toList());

			String resourceContent = JsonUtils.getMapper().writeValueAsString(tomtomEventList);

//			JsonNode jsonNode = JsonUtils.getMapper().valueToTree("");
//			String recordsContent = jsonNode.toString();
			String similarDateTime = StringTools.findFirstMatchSequence(resourceContent, DateUtils.DATE_SIMILAR_REGEX);
			Date srcUpdateTime = ClassUtils.isValid(similarDateTime) ? DateUtils.parseStrToDate(similarDateTime)
					: updateTime;
			log.info("Resource fetch spent {}ms", System.currentTimeMillis() - updateTime.getTime());

			return ETLHelper.buildExtractResult(resourceInfo, resourceContent, srcUpdateTime, updateTime);
		} catch (ResourceNotUpdateException | ResourceFormatErrorException e) {
			throw e;
		} catch (Exception e) {
			throw new ResourceException(e);
		}
	}

}
