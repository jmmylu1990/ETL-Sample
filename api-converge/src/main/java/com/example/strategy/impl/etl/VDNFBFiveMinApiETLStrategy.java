package com.example.strategy.impl.etl;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.JsonNode;
import com.example.exception.ResourceException;
import com.example.exception.ResourceFormatErrorException;
import com.example.exception.ResourceNotUpdateException;
import com.example.model.dto.etl.ExtractResult;
import com.example.model.dto.etl.ResourceInfo;
import com.example.model.dto.source.traffic.live.VDFiveMinForNFB;
import com.example.model.dto.source.traffic.live.wrap.Info;
import com.example.utils.DateUtils;
import com.example.utils.DownloadHelper;
import com.example.utils.ETLHelper;
import com.example.utils.FileOperationUtils;
import com.example.utils.GzUtils;
import com.example.utils.JsonUtils;
import com.example.utils.XmlUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("VDNFBFiveMinApiETLStrategy")
public class VDNFBFiveMinApiETLStrategy extends GeneralApiETLStrategy {

	@Override
	public ExtractResult extract(ResourceInfo resourceInfo) throws ResourceException {
		try {
			// Step 1: Check resource available or not
			Date updateTime = new Date();

			Date srcUpdateTime = updateTime;
			Date fileTime = DateUtils.addMinutes(updateTime, -15);
			String fileDateStr = DateUtils.formatDateToStr(DateUtils.SIMPLE_DATE_FORMAT, fileTime);
			String fileTimeStr = DateUtils.formatDateToStr("HHmm", fileTime);
			//String fileTimeStr = "1300";
			Date fileDate = DateUtils.parseStrToDate(fileDateStr);
			String resource = String.format("http://tisvcloud.freeway.gov.tw/history/vd/%s/vd_value5_%s.xml.gz",
					fileDateStr, fileTimeStr);

			log.info("Resource: `{}`", resource);

			File path = ETLHelper.getResultOutputPath(resourceInfo.getRootPath(), resourceInfo.getRelativePath(),
					srcUpdateTime, "/xml").toFile();

			DownloadHelper.downloadVDFile(resource, fileDate, path.toString());

			File gzFile = FileOperationUtils.contains(path, String.format("vd_value5_%s.xml.gz", fileTimeStr));

			File decompressedFile = GzUtils.decompressGzOnly(gzFile, path.toString());

			String jsonStr = JsonUtils.toJsonString(decompressedFile);
			JsonNode tree = XmlUtils.getMapper().readTree(jsonStr);
			List<Info> InfoList = JsonUtils.toBeanList(tree.get("Infos").get("Info").toString(), Info.class);
			List<VDFiveMinForNFB> vdFiveMinForNFBList = InfoList.stream().map(item -> {
				List<VDFiveMinForNFB> vdFiveMinForNFBs = item.getLanes().stream().map(lane -> {

					VDFiveMinForNFB vdFiveMinForNFB = new VDFiveMinForNFB();
					vdFiveMinForNFB.setVdID(item.getVdID());
					vdFiveMinForNFB.setStatus(item.getStatus());
					vdFiveMinForNFB.setVsrDir(lane.getVsrDir());
					vdFiveMinForNFB.setVsrID(lane.getVsrID());
					vdFiveMinForNFB.setSpeed(lane.getSpeed());
					vdFiveMinForNFB.setLaneoccupy(lane.getLaneoccupy());
					vdFiveMinForNFB.setDataCollectTime(item.getDatacollectTime());
					lane.getCars().forEach(car -> {

						if (car.getCarID().equals("S")) {
							vdFiveMinForNFB.setSVolume(car.getVolume());
						} else if (car.getCarID().equals("T")) {
							vdFiveMinForNFB.setTVolume(car.getVolume());
						} else if (car.getCarID().equals("L")) {
							vdFiveMinForNFB.setLVolume(car.getVolume());
						} else if (car.getCarID().equals("M")) {
							vdFiveMinForNFB.setMVolume(car.getVolume());
						}
					});

					return vdFiveMinForNFB;
				}).collect(Collectors.toList());

				return vdFiveMinForNFBs;
			}).flatMap(list -> list.stream()).collect(Collectors.toList());

			String resourceContent = JsonUtils.getMapper().writeValueAsString(vdFiveMinForNFBList);
			log.info("Resource fetch spent {}ms", System.currentTimeMillis() - updateTime.getTime());

			return ETLHelper.buildExtractResult(resourceInfo, resourceContent, srcUpdateTime, updateTime);
		} catch (ResourceNotUpdateException | ResourceFormatErrorException e) {
			throw e;
		} catch (Exception e) {
			throw new ResourceException(e);
		}
	}
}
