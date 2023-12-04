package com.example.strategy.impl.etl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import com.example.exception.ResourceException;
import com.example.exception.ResourceFormatErrorException;
import com.example.exception.ResourceNotUpdateException;
import com.example.model.dto.etl.ExtractResult;
import com.example.model.dto.etl.ResourceInfo;
import com.example.model.dto.source.traffic.device.VDOneLandMin;
import com.example.model.dto.source.traffic.live.VDLiveForCheck;
import com.example.model.dto.source.traffic.live.VDLiveVehicleForCheck;
import com.example.model.enums.DbSourceEnum;
import com.example.utils.ETLHelper;
import com.example.utils.JsonUtils;
import com.example.utils.SqlUtils;
import com.example.utils.StringTools;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("VDOneLandMinStrategy")
public class VDOneLandMinStrategy extends GeneralApiETLStrategy {

	@Value("${VDOneLandMin.speedmax}")
	private Integer speedmax;
	@Value("${VDOneLandMin.speedmin}")
	private Integer speedmin;
	@Value("${VDOneLandMin.volumnmax}")
	private Integer volumnmax;
	@Value("${VDOneLandMin.volumnmin}")
	private Integer volumnmin;
	@Value("${VDOneLandMin.occupymax}")
	private Integer occupymax;
	@Value("${VDOneLandMin.occupymin}")
	private Integer occupymin;

	@Value("${vdOneLandMin.insertOneLandMin}")
	private String insertSQL;

	@Autowired
	@Qualifier("jdbcTemplateMap")
	private Map<DbSourceEnum, NamedParameterJdbcTemplate> jdbcTemplateMap;

	@Override
	public ExtractResult extract(ResourceInfo resourceInfo) throws ResourceException {
		try {
			// Step 1: Check resource available or not
			Date updateTime = new Date();
			String resource = resourceInfo.getResource();
			log.info("Resource: `{}`", resource);

			// Step 2: Extract the array part of resource
			
			String resourceContent = JsonUtils.toJsonString(resource);

			List<VDLiveForCheck> vdLiveList = JsonUtils.toBeanList(resourceContent, VDLiveForCheck.class);

			List<VDOneLandMin> vdOneLandMins = vdLiveList.stream().map(vdLive -> {
				return vdLive.getLinkFlows().stream().map(linkFlow -> {

					return linkFlow.getLanes().stream().map(lane -> {
						VDOneLandMin vdOneLandMin = new VDOneLandMin();
						vdOneLandMin.setVdID(vdLive.getVdID());
						vdOneLandMin.setLinkID(linkFlow.getLinkID());
						vdOneLandMin.setVsrID(lane.getLaneID());
						vdOneLandMin.setSpeed(lane.getSpeed());
						vdOneLandMin.setOcc(lane.getOccupancy());
						vdOneLandMin.setDataCollectTime(vdLive.getDataCollectTime());
						List<VDLiveVehicleForCheck> Vehicles = lane.getVehicles();
						Vehicles.forEach(vehicle -> {
							if (vehicle.getVehicleType().equals("L")) {
								vdOneLandMin.setLVolume(vehicle.getVolume());
							} else if (vehicle.getVehicleType().equals("M")) {
								vdOneLandMin.setMVolume(vehicle.getVolume());
							} else if (vehicle.getVehicleType().equals("S")) {
								vdOneLandMin.setSVolume(vehicle.getVolume());
							} else if (vehicle.getVehicleType().equals("T")) {
								vdOneLandMin.setTVolume(vehicle.getVolume());
							}
						});

						return vdOneLandMin;
					}).collect(Collectors.toList());
				}).flatMap(List::stream).collect(Collectors.toList());
			}).flatMap(List::stream).collect(Collectors.toList());
			List<String> errDiagStr = new ArrayList<String>();

			vdOneLandMins.forEach(vdOneLandMin -> {

				Integer averageVolime = vdOneLandMin.getSVolume();

				if (vdOneLandMin.getSpeed() > speedmax) {
					errDiagStr.add("101");
				} else if (vdOneLandMin.getSpeed() < speedmin) {
					errDiagStr.add("102");
				}

				if (averageVolime > volumnmax) {
					errDiagStr.add("103");
				} else if (averageVolime < volumnmin) {
					errDiagStr.add("104");
				}

				if ((averageVolime == 0 && vdOneLandMin.getSpeed() != 0)
						|| (averageVolime == 0 && vdOneLandMin.getOcc() != 0)) {
					errDiagStr.add("201");
				}

				if (vdOneLandMin.getOcc() > occupymax) {
					errDiagStr.add("105");
				} else if (vdOneLandMin.getOcc() < occupymin) {
					errDiagStr.add("106");
				}

				if ((vdOneLandMin.getOcc() == 0 && averageVolime != 0)
						|| (vdOneLandMin.getOcc() == 0 && vdOneLandMin.getSpeed() != 0)) {
					errDiagStr.add("202");
				}

				if (vdOneLandMin.getSpeed() != 0 && vdOneLandMin.getOcc() != 0 && averageVolime != 0) {

					Double lengthOfCar = 10 * vdOneLandMin.getSpeed() * vdOneLandMin.getOcc() / (60 * averageVolime);

					if (lengthOfCar < 2.2 || lengthOfCar > 18) {
						errDiagStr.add("207");
					}
				}

				if (errDiagStr.size() == 0) {

					vdOneLandMin.setErrDiag("diag0");

				} else {
					vdOneLandMin
							.setErrDiag(StringTools.join(",", errDiagStr.toString()).replace("[", "").replace("]", ""));

				}

				errDiagStr.clear();
			});

			String contentForCheck = JsonUtils.getMapper().writeValueAsString(vdOneLandMins);
			Date srcUpdateTime = updateTime;
			log.info("Resource fetch spent {}ms", System.currentTimeMillis() - updateTime.getTime());

			return ETLHelper.buildExtractResult(resourceInfo, contentForCheck, srcUpdateTime, updateTime);
		} catch (ResourceNotUpdateException |

				ResourceFormatErrorException e) {
			throw e;
		} catch (Exception e) {
			throw new ResourceException(e);
		}
	}

	@Override
	public void successCallback() {
		log.info("DbSource: {} / Procedure: {}", DbSourceEnum.MY_SQL, insertSQL);
		JdbcTemplate jdbcTemplate = jdbcTemplateMap.get(DbSourceEnum.MY_SQL).getJdbcTemplate();
		jdbcTemplate.execute(SqlUtils.clean(insertSQL));

	}

}
