package com.example.strategy.impl.etl;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import com.example.exception.ResourceException;
import com.example.exception.ResourceFormatErrorException;
import com.example.exception.ResourceNotUpdateException;
import com.example.model.dto.etl.ExtractResult;
import com.example.model.dto.etl.ResourceInfo;
import com.example.model.dto.source.ntpc.HolidayListDetail;
import com.example.utils.DateUtils;
import com.example.utils.ETLHelper;
import com.example.utils.JsonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("HolidayListDetailApiETLStrategy")
public class HolidayListDetailApiETLStrategy extends GeneralApiETLStrategy {

	@Override
	public ExtractResult extract(ResourceInfo resourceInfo) throws ResourceException {
		try {
			// Step 1: Check resource available or not
			Date updateTime = new Date();
			String resource = resourceInfo.getResource();

			String thisYear = DateUtils.formatDateToStr(DateUtils.YEAR_FORMAT, updateTime);

			Date startDate = DateUtils.parseStrToDate(String.format("%s-01-01", thisYear));

			Date endDate = DateUtils.parseStrToDate(String.format("%s-12-31", thisYear));

			List<Date> dates = DateUtils.getDateInterval(startDate, endDate);
			
			// Step 2: Extract the array part of resource
			
			// 取得今年的假日資料
			List<HolidayListDetail> thisYearHolidayDetails = this.convertJsonList(0, resource, new ArrayList<>())
			.stream().filter(item -> {
				Date dataDate = item.getDataDate();
				String dataDateYear = DateUtils.formatDateToStr(DateUtils.YEAR_FORMAT, dataDate);
				return dataDateYear.equals(thisYear);
			}).collect(Collectors.toList());

			// 取得今年的平日資料
			List<HolidayListDetail> workdayList = dates.stream().filter(date -> {
				return thisYearHolidayDetails.stream().noneMatch(holidayDate-> holidayDate.getDataDate().equals(date));
			}).map(weekday -> {
				HolidayListDetail holidayListDetail = new HolidayListDetail();
				holidayListDetail.setDataDate(weekday);
				holidayListDetail.setIsHoliday("否");
				return holidayListDetail;

			}).collect(Collectors.toList());
			List<HolidayListDetail> thisYearDetailList = 
				Stream.concat(thisYearHolidayDetails.stream(), workdayList.stream())
					.map(holidayListDetail-> {
						Date dataDate = holidayListDetail.getDataDate();
						String yearMonth = DateUtils.formatDateToStr(DateUtils.SIMPLE_YEAR_MONTH_FORMAT, dataDate);
						String lastYearMonth = DateUtils.formatDateToStr(DateUtils.SIMPLE_YEAR_MONTH_FORMAT, DateUtils.addMonths(dataDate, -1));
						holidayListDetail.setYearMonth(yearMonth);
						holidayListDetail.setLastYearMonth(lastYearMonth);
						holidayListDetail.setWeekday(this.getWeekDayNameForZh(dataDate));
						return Objects.nonNull(holidayListDetail.getHolidayCategory()) ? this.getType(holidayListDetail) : holidayListDetail;
					})
					  .collect(Collectors.toList());
			String content = JsonUtils.getMapper().writeValueAsString(thisYearDetailList);
//			String resourceArrayPart = content.substring(content.indexOf('['), content.lastIndexOf(']') + 1);
			Date srcUpdateTime = updateTime;
			log.info("Resource fetch spent {}ms", System.currentTimeMillis() - updateTime.getTime());
			return ETLHelper.buildExtractResult(resourceInfo, content, srcUpdateTime, updateTime);
		} catch (ResourceNotUpdateException | ResourceFormatErrorException e) {
			throw e;
		} catch (Exception e) {
			throw new ResourceException(e);
		}
	}

	public List<HolidayListDetail> convertJsonList(int page, String resource, List<HolidayListDetail> arrayList) throws Exception {
		String resourceUrl = resource + page;
		String resourceContent = JsonUtils.toJsonString(resourceUrl);
		List<HolidayListDetail> resourceList = JsonUtils.toBeanList(resourceContent, HolidayListDetail.class);
		if (!resourceList.isEmpty()) {
			log.info("Resource: `{}`", resourceUrl);
			arrayList.addAll(resourceList);
			return convertJsonList(++page, resource, arrayList);
		}
		return arrayList;
	}

	public String getWeekDayNameForZh(Date date) {
		String[] weekDay = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int weekIndex = calendar.get(Calendar.DAY_OF_WEEK) - 1;

		return weekDay[weekIndex];

	}

	public HolidayListDetail getType(HolidayListDetail holidayListDetail) {

		switch (holidayListDetail.getHolidayCategory()) {

		case "放假之紀念日及節日":
			holidayListDetail.setType01("Y");
			break;
		case "星期六、星期日":
			holidayListDetail.setType02("Y");
			break;
		case "星期日":
			holidayListDetail.setType03("Y");
			break;
		case "紀念日及節日":
			holidayListDetail.setType04("Y");
			break;
		case "特定節日":
			holidayListDetail.setType05("Y");
			break;
		case "補行上班日":
			holidayListDetail.setType06("Y");
			break;
		case "補假":
			holidayListDetail.setType07("Y");
			break;
		case "調整放假日":
			holidayListDetail.setType08("Y");
			break;
		case "寒暑假":
			holidayListDetail.setType09("Y");
			break;
		case "颱風假":
			holidayListDetail.setType10("Y");
			break;

		}

		return holidayListDetail;
	}

}
