package com.example.strategy.impl.etl;

import com.fasterxml.jackson.databind.JsonNode;
import com.example.exception.ResourceException;
import com.example.exception.ResourceFormatErrorException;
import com.example.exception.ResourceNotUpdateException;
import com.example.model.dto.etl.ExtractResult;
import com.example.model.dto.etl.ResourceInfo;
import com.example.model.dto.source.other.RoadExcavation;
import com.example.model.dto.source.other.RoadExcavationMinGuo;
import com.example.utils.DateUtils;
import com.example.utils.ETLHelper;
import com.example.utils.JsonUtils;
import com.example.utils.XmlUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.Chronology;
import java.time.chrono.MinguoChronology;
import java.time.chrono.MinguoDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DecimalStyle;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Slf4j
@Component("RoadExcavationApiETLStrategy")
public class RoadExcavationApiETLStrategy extends GeneralApiETLStrategy{
    @Override
    public ExtractResult extract(ResourceInfo resourceInfo) throws ResourceException {
        try {
            // Step 1: Check resource available or not
            Date updateTime = new Date();
            String resource = resourceInfo.getResource();
            log.info("Resource: `{}`", resource);

            // Step 2: Extract the array part of resource
            JsonNode jsonNode = XmlUtils.getMapper().readTree(JsonUtils.toJsonString(resource));
            String resourceContent = JsonUtils.getMapper().writeValueAsString(jsonNode);
            String resourceArrayPart = resourceContent.substring(resourceContent.indexOf('['), resourceContent.lastIndexOf(']') + 1).replace("　", "");
            List<RoadExcavationMinGuo> roadExcavationList = JsonUtils.toBeanList(resourceArrayPart, RoadExcavationMinGuo.class);

            roadExcavationList.forEach(s->{

                if(s.getRmtDate()!=null)
                    s.setRmtDate(DateUtils.parseStrToDate(transferMinguoDateToADDate(DateUtils.formatDateToStr("yyyyMMdd",s.getRmtDate()))));
                if(s.getDateDigS()!=null)
                    s.setDateDigS(DateUtils.parseStrToDate(transferMinguoDateToADDate(DateUtils.formatDateToStr("yyyyMMdd",s.getDateDigS()))));
                if(s.getDateDigE()!=null)
                    s.setDateDigE(DateUtils.parseStrToDate(transferMinguoDateToADDate(DateUtils.formatDateToStr("yyyyMMdd",s.getDateDigE()))));
                if(s.getDateExtS()!=null)
                    s.setDateExtS(DateUtils.parseStrToDate(transferMinguoDateToADDate(DateUtils.formatDateToStr("yyyyMMdd",s.getDateExtS()))));
                if(s.getDateExtE()!=null)
                    s.setDateExtE(DateUtils.parseStrToDate(transferMinguoDateToADDate(DateUtils.formatDateToStr("yyyyMMdd",s.getDateExtE()))));

            });

            log.info("Resource fetch spent {}ms", System.currentTimeMillis() - updateTime.getTime());

            return ETLHelper.buildExtractResult(resourceInfo, JsonUtils.getMapper().writeValueAsString(roadExcavationList), updateTime, updateTime);
        } catch (ResourceNotUpdateException | ResourceFormatErrorException e) {
            throw e;
        } catch (Exception e) {
            throw new ResourceException(e);
        }
    }

    public static String transferADDateToMinguoDate(String dateString) {
        LocalDate localDate = LocalDate.parse(dateString, DateTimeFormatter.ofPattern("yyyMMdd"));
        return MinguoDate.from(localDate).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }

    public static String transferMinguoDateToADDate(String dateString) {
        Chronology chrono = MinguoChronology.INSTANCE;
        DateTimeFormatter df = new DateTimeFormatterBuilder().parseLenient()
                .appendPattern("yyyMMdd")
                .toFormatter()
                .withChronology(chrono)
                .withDecimalStyle(DecimalStyle.of(Locale.getDefault()));

        ChronoLocalDate chDate = chrono.date(df.parse(dateString));
        return LocalDate.from(chDate).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }
}
