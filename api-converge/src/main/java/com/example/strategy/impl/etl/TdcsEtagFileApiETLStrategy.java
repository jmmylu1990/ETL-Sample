package com.example.strategy.impl.etl;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.component.ApiRewriteComponent;
import com.example.exception.ResourceException;
import com.example.exception.ResourceFormatErrorException;
import com.example.exception.ResourceNotUpdateException;
import com.example.model.dto.etl.EncapsulationFile;
import com.example.model.dto.etl.ExtractResult;
import com.example.model.dto.etl.ResourceInfo;
import com.example.model.dto.etl.TransformResult;
import com.example.model.dto.source.tdcs.TdcsResoure;
import com.example.utils.ClassUtils;
import com.example.utils.DateUtils;
import com.example.utils.DownloadHelper;
import com.example.utils.ETLHelper;
import com.example.utils.FileOperationUtils;
import com.example.utils.GzUtils;
import com.example.utils.HttpUtils;
import com.example.utils.JsonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("TdcsEtagFileApiETLStrategy")
public class TdcsEtagFileApiETLStrategy extends GeneralApiETLStrategy {

	@Autowired
	private ApiRewriteComponent apiRewriteComponent;
	
	@Override
	public ExtractResult extract(ResourceInfo resourceInfo) throws ResourceException {
		try {
			// Step 1: Check resource available or not
			Date updateTime = new Date();
			String resourceWebPage = apiRewriteComponent.normalize(resourceInfo.getResource());
			Date srcUpdateTime = DateUtils.parseStrToDate(resourceWebPage.substring(resourceWebPage.lastIndexOf('/') + 1));
			log.info("Resource page: `{}`", resourceWebPage);
			
			// Traversal 0 ~ 23 hour per day
			List<TdcsResoure> tdcsResourceList = new ArrayList<>();
			IntStream.range(0, 24).forEach(hour -> {
				String fileDirUri = String.format("%s/%02d/", resourceWebPage, hour);
				try (CloseableHttpClient httpClient = (CloseableHttpClient) HttpUtils.createHttpClient();
						CloseableHttpResponse response = httpClient.execute(HttpUtils.toHttpRequest(fileDirUri))) {
					if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
						Document doc = Jsoup.parse(response.getEntity().getContent(), StandardCharsets.UTF_8.name(), fileDirUri);
						//Elements elements = doc.select(".table.style-1 > tbody > tr");
						Elements elements = doc.select(".list-box > section > table > tbody");
						elements.stream().forEach(elem -> {
							String fileUrl = elem.selectFirst("td a").absUrl("href");
							String srcUpdateTimeStr = elem.selectFirst("td:eq(1)").text();
							if (ClassUtils.isValid(srcUpdateTimeStr)) tdcsResourceList.add(new TdcsResoure(DateUtils.parseStrToDate(srcUpdateTimeStr), fileUrl));
						});
					}
				} catch (IOException e) {
					log.error(e.getMessage(), e);
				}
			});

			String resourceContent = JsonUtils.getMapper().writeValueAsString(tdcsResourceList);
			log.info("Valid resource num: `{}`", tdcsResourceList.size());
			log.info("Resource fetch spent {}ms", System.currentTimeMillis() - updateTime.getTime());
			
			return ETLHelper.buildExtractResult(resourceInfo, resourceContent, srcUpdateTime, updateTime);
		} catch (ResourceNotUpdateException | ResourceFormatErrorException e) {
			throw e;
		} catch (Exception e) {
			throw new ResourceException(e);
		}
	}

	@Override
	public TransformResult transform(ResourceInfo resourceInfo, ExtractResult extractResult) throws ResourceFormatErrorException {
		long startTime = System.currentTimeMillis();
		// Step 1: Prepare the infomation that need to import
		Map<String, EncapsulationFile> importFileMap = new HashMap<>();
		List<File> resources = extractResult.getResources();
		File firstResource = resources.get(0);
		String content = FileOperationUtils.extractContent(firstResource);
		List<TdcsResoure> tdcsResourceList = JsonUtils.toBeanList(content, TdcsResoure.class);
		String outputPath = firstResource.getParentFile().getParent();
		File stagePath = new File(outputPath, "stage");
		Date srcUpdateTime = extractResult.getSrcUpdateTime();
		String targetTable = resourceInfo.getTargetTable();
		String tableName = targetTable.substring(targetTable.indexOf('.') + 1);
		String simpleTimestamp = DateUtils.formatDateToStr(DateUtils.SIMPLE_DATETIME_FORMAT, srcUpdateTime);
		
		// Step 2: Fetch all specified resource
		tdcsResourceList.stream().forEach(tdcsResource -> {
			try {
				String url = tdcsResource.getUrl();
				String fileName = url.substring(url.lastIndexOf('/') + 1); 
				// Put downlaod file to stage path
				File stageFile = new File(stagePath, fileName);
				DownloadHelper.download(url, stageFile);
			} catch (IOException e) {
				log.error(e.getMessage(), e);
			}
		});
		
		try {
			// Step 3: Merge all stage file and generate the encapsulation file instance
			String fileName = String.format("%s_%s.csv", tableName, simpleTimestamp);
			File outputFile = new File(outputPath, fileName);
			if (outputPath.contains("M06")) {
				List<File> fileList = FileOperationUtils.mergeTDCSM06Files(stagePath, outputFile, false);
				File outputDetailFile = fileList.get(1);
				long lineCount = FileOperationUtils.getLineCount(outputDetailFile);
				// Compress files after line calculation
				fileList.forEach(file -> GzUtils.compressToGzFile(outputFile, true));
				EncapsulationFile detailEncapsulationFile = new EncapsulationFile(outputDetailFile, lineCount);
				importFileMap.put(resourceInfo.getTargetTable(), detailEncapsulationFile);
			} else {
				FileOperationUtils.mergeTDCSFiles(stagePath, outputFile, false);
			}
			long lineCount = FileOperationUtils.getLineCount(outputFile);
			EncapsulationFile masterEncapsulationFile = new EncapsulationFile(outputFile, lineCount);
			importFileMap.put(resourceInfo.getTargetTable(), masterEncapsulationFile);
			log.info("Extract result transform spent {}ms", System.currentTimeMillis() - startTime);
			
			return TransformResult.builder()
					.importFileMap(importFileMap)
					.build();
		} catch (Exception e) {
			throw new ResourceFormatErrorException(e);
		}
	}
	
}
