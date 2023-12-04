package com.example.strategy.impl.etl;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.example.exception.ResourceException;
import com.example.exception.ResourceFormatErrorException;
import com.example.exception.ResourceNotUpdateException;
import com.example.factory.FileImportStrategyFactory;
import com.example.model.dto.etl.ExtractResult;
import com.example.model.dto.etl.ResourceInfo;
import com.example.model.dto.source.other.OilPrice;
import com.example.utils.DateUtils;
import com.example.utils.ETLHelper;
import com.example.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("OilPriceApiETLStrategy")
public class OilPriceApiETLStrategy extends GeneralApiETLStrategy {

	@Autowired
	protected FileImportStrategyFactory fileImportStrategyFactory;

	@Override
	public ExtractResult extract(ResourceInfo resourceInfo) throws ResourceException {
		try {
			// Step 1: Check resource available or not
			Date updateTime = new Date();
			// Since there is no timestamp similar value in api, so assign the same with
			// `updateTime`
			String resource = resourceInfo.getResource();
			log.info("Resource: `{}`", resource);
			// Parse web content (html)
			Document document = Jsoup.parse(JsonUtils.toJsonString(resource));
			Elements elements = document.select("table > tbody > tr > td > span");
			
			Date srcUpdateTime = DateUtils.parseStrToDate(elements.get(1).text().replace("實施日期:", ""));
			
			 List<String> productLabels = elements.stream().filter(s->s.toString().contains("MyGridView")).collect(Collectors.toList())
					.stream().map(s->{
						String productLabel = s.id().substring(0, s.id().lastIndexOf("_"));
					return productLabel;
					}).distinct().collect(Collectors.toList());
			 
			 List<OilPrice> OilPriceList = productLabels.stream().map(s->{
				
				 OilPrice oilPrice = new OilPrice();
				 oilPrice.setProductNo(document.getElementById(String.format("%s_%s",s, "產品編號")).text());
				 oilPrice.setProductName(document.getElementById(String.format("%s_%s",s, "產品名稱")).text());
				 oilPrice.setProductPackage(document.getElementById(String.format("%s_%s",s, "包裝")).text());
				 oilPrice.setSalesTarget(document.getElementById(String.format("%s_%s",s, "銷售對象")).text());
				 oilPrice.setTradingLocation(document.getElementById(String.format("%s_%s",s, "交貨地點")).text());
				 oilPrice.setSalesUnit(document.getElementById(String.format("%s_%s",s, "計價單位")).text());
				 oilPrice.setReferencePrice(Double.parseDouble(document.getElementById(String.format("%s_%s",s, "參考牌價")).text().replace(",", "")));
				 oilPrice.setBusinessTax(document.getElementById(String.format("%s_%s",s, "營業稅")).text());
				 oilPrice.setCommodityTax(document.getElementById(String.format("%s_%s",s, "貨物稅")).text());
				 oilPrice.setRemark(document.getElementById(String.format("%s_%s",s, "備註")).text());
				 return oilPrice;
			 }).collect(Collectors.toList());
			 
				String resourceContent = JsonUtils.getMapper().writeValueAsString(OilPriceList);
					
			log.info("Resource fetch spent {}ms", System.currentTimeMillis() - updateTime.getTime());

			return ETLHelper.buildExtractResult(resourceInfo, resourceContent, srcUpdateTime, updateTime);
		} catch (ResourceNotUpdateException | ResourceFormatErrorException e) {
			throw e;
		} catch (Exception e) {
			throw new ResourceException(e);
		}
	}

}
