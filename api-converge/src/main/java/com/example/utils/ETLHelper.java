package com.example.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import java.util.zip.Deflater;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.beanutils.BeanUtils;

import com.example.exception.ResourceException;
import com.example.exception.ResourceNotUpdateException;
import com.example.model.dto.etl.DetailModel;
import com.example.model.dto.etl.EncapsulationFile;
import com.example.model.dto.etl.ExtractResult;
import com.example.model.dto.etl.ResourceInfo;
import com.example.model.entity.mysql.JobExtraParam;
import com.example.model.entity.mysql.ScheduleJob;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class ETLHelper {

	public final ExtractResult buildExtractResult(ResourceInfo resourceInfo, String backupContent, Date srcUpdateTime,
			Date updateTime) throws ResourceException {
		// Check resource content at first
		if (!ClassUtils.isValid(backupContent) || backupContent.equals("[]")) {
			throw new ResourceException("Resource json array is empty!");
		}
		String targetTable = resourceInfo.getTargetTable();
		String tableName = targetTable.substring(targetTable.indexOf('.') + 1);
		String simpleTimestamp = DateUtils.formatDateToStr(DateUtils.SIMPLE_DATETIME_FORMAT, srcUpdateTime);

		File path = getResultOutputPath(resourceInfo.getRootPath(), resourceInfo.getRelativePath(), srcUpdateTime).toFile();
		String fileName = String.format("%s_%s.src", tableName, simpleTimestamp.replaceAll("\\D", ""));
		File resourceContentFile = Paths.get(path.getAbsolutePath(), "source", fileName).toFile();
		if (resourceContentFile.exists()) {
			throw new ResourceNotUpdateException("Resource backup is existed!");
		}
		FileOperationUtils.generateTextFile(resourceContentFile, backupContent);

		return ExtractResult.builder().resources(Arrays.asList(resourceContentFile)).srcUpdateTime(srcUpdateTime)
				.updateTime(updateTime).build();
	}

	public final EncapsulationFile buildEncapsulationFile(ExtractResult extractResult, String targetTable,
			List<?> resultList) {
		// Map entity list to file
		List<File> resources = extractResult.getResources();
		String outputPath = resources.get(0).getParentFile().getParent();
		String simpleTimestamp = DateUtils.formatDateToStr(DateUtils.SIMPLE_DATETIME_FORMAT,
				extractResult.getSrcUpdateTime());
		String tableName = targetTable.substring(targetTable.indexOf('.') + 1);
		String fileContent = resultList.parallelStream().map(data -> StringTools.toCsvString(data, StringTools.TAB))
				.flatMap(List::stream).collect(Collectors.joining(StringTools.CRLF, "", StringTools.CRLF));

		// Prepare the infomation that need to import
		String fileName = String.format("%s_%s.csv", tableName, simpleTimestamp);
		File outputFile = FileOperationUtils.generateTextFile(new File(outputPath, fileName), fileContent);
		long lineCount = FileOperationUtils.getLineCount(outputFile);

		return new EncapsulationFile(outputFile, lineCount);
	}

	public Path getResultOutputPath(String basePath, String relativePath, Date date, String... morePath) {
		String dateStr = String.format("%tF", date);
		String[] pathSegments = new String[] { relativePath, dateStr.substring(0, 4), // Year
				dateStr.substring(0, 7), // Year-Month
				dateStr.substring(0, 10) // Date,
		};
		String[] allPathSegments = Stream.concat(Arrays.stream(pathSegments), Arrays.stream(morePath))
				.toArray(String[]::new);
		return Paths.get(basePath, allPathSegments);
	}

	public boolean compressETLResult(ScheduleJob job, String basePath, Date processDate, boolean removeResource) {
		String jobName = job.getJobName();
		JobExtraParam jobParam = job.getJobExtraParam();
		if (Objects.isNull(jobParam))
			return false;

		File resultOutputPath = ETLHelper.getResultOutputPath(basePath, jobParam.getRelativePath(), processDate).toFile();
		if (resultOutputPath.exists()) {
			long startTime = System.currentTimeMillis();
			// Traversal and collect all *.csv file
			List<File> csvFileList = FileOperationUtils.traversalAllFiles(resultOutputPath, "csv");
			// Merge every file as a single file
			Map<String, List<File>> fileGroupList = csvFileList.stream()
					.collect(Collectors.groupingBy(file -> file.getName().replaceAll("\\d{6}\\.csv", ".csv.gz")));
			fileGroupList.forEach((finalName, fileList) -> {
				File mergedFile = new File(resultOutputPath, finalName);
				try (FileOutputStream fos = new FileOutputStream(mergedFile, true);
					OutputStream out = new GZIPOutputStream(fos, 8192) {{ def.setLevel(Deflater.BEST_COMPRESSION); }}
				) {
					log.info("[`{}`] {} available csv file(s) found", finalName, fileList.size());
					for (File file : fileList) {
						Files.copy(file.toPath(), out);
						FileOperationUtils.remove(file);
					}
					out.flush();
				} catch (IOException e) {
					log.error(e.getMessage(), e);
				}
			});
			// Compress source dictionary with `Tar.Gzip` type
			File sourcePath = new File(resultOutputPath, "source");
			if (sourcePath.exists())
				GzUtils.compressToTarGzFile(sourcePath, removeResource);

			String spentTimeDesc = DateUtils.formatUsageTime(System.currentTimeMillis() - startTime);
			log.info("[{}] 介接資料合併壓縮花費時間: {}", jobName, spentTimeDesc);

			return true;
		}

		return false;
	}

	public final <S, T extends DetailModel> void copyProperties(AtomicInteger counter, S master, Iterable<T> details) {
		StreamSupport.stream(details.spliterator(), false).forEach(detail -> {
			try {
				BeanUtils.copyProperties(detail, master);
				DetailModel.class.cast(detail).setUuid(counter.getAndIncrement()); // Set UUID (i.e. sequence)
			} catch (IllegalAccessException | InvocationTargetException e) {
				log.error(e.getMessage(), e);
			}
		});
	}
}
