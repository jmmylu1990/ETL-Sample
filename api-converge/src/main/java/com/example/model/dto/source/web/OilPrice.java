package com.example.model.dto.source.web;

import java.io.Serializable;
import java.util.Date;

import com.example.utils.DateUtils;

import lombok.Data;

/**  CodeGen DTO for `OilPrice` **/
public @Data class OilPrice implements Serializable {

	private static final long serialVersionUID = 1L;

	private String productNo;

	private String productName;

	private String productPackage;

	private String salesTarget;

	private String tradingLocation;

	private String salesUnit;

	private Double referencePrice;

	private String businessTax;

	private String commodityTax;

	private String remark;

	private Date srcUpdateTime;

	private Date updateTime;

	private Date infoTime;

	private Date infoDate;

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(productNo).append("\t").append(productName).append("\t").append(productPackage).append("\t")
				.append(salesTarget).append("\t").append(tradingLocation).append("\t").append(salesUnit).append("\t")
				.append(referencePrice).append("\t").append(businessTax).append("\t").append(commodityTax).append("\t").append(remark).append("\t")
				.append(DateUtils.formatDateToStr(srcUpdateTime)).append("\t")
				.append(DateUtils.formatDateToStr(updateTime)).append("\t")
				.append(DateUtils.formatDateToStr(infoTime)).append("\t")
				.append(DateUtils.formatDateToStr(DateUtils.DASHED_DATE_FORMAT, infoDate));
		return builder.toString();
	}
	
}