package com.example.component;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.dao.mysql.DatasetRepository;
import com.example.dao.mysql.ImpalaImportConfigRepository;
import com.example.dao.mysql.ImpalaImportLogRepository;
import com.example.dao.mysql.ScheduleJobRepository;
import com.example.factory.FileImportStrategyFactory;
import com.example.model.entity.mysql.Dataset;
import com.example.model.entity.mysql.ImpalaImportConfig;
import com.example.model.entity.mysql.ImpalaImportLog;
import com.example.model.entity.mysql.JobExtraParam;
import com.example.model.entity.mysql.ScheduleJob;
import com.example.model.enums.DbSourceEnum;
import com.example.strategy.FileImportStrategy;
import com.example.utils.DateUtils;
import com.example.utils.ETLHelper;
import com.example.utils.FileOperationUtils;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ETLToolComponent {

    @Value("${root.path}")
    private String rootPath;

    @Autowired
    private ScheduleJobRepository scheduleJobRepository;
    @Autowired
    private ImpalaImportConfigRepository impalaImportConfigRepository;
    @Autowired
    private ImpalaImportLogRepository impalaImportLogRepository;
    @Autowired
    private FileImportStrategyFactory fileImportStrategyFactory;


    @Autowired
    private DatasetRepository datasetRepository;

    @Autowired
    @Qualifier("jdbcTemplateMap")
    private Map<DbSourceEnum, NamedParameterJdbcTemplate> jdbcTemplateMap;

    public long compressETLResult(String jobId, Date infoDate, boolean removeResource) {
        ScheduleJob job = scheduleJobRepository.findByJobId(jobId);
        return ETLHelper.compressETLResult(job, rootPath, infoDate, removeResource) ? 1 : 0;
    }

    public long compressAllETLResult(Date infoDate, boolean removeResource) {
        List<ScheduleJob> jobList = scheduleJobRepository.findByJobGroup("ETL-Group");
        return jobList.parallelStream().mapToLong(job -> {
        	try {
        		return this.compressETLResult(job.getJobId(), infoDate, removeResource);
        	} catch (Exception e) {
        		log.error(e.getMessage(), e);
        	}
        	return 0L;
        })
        .sum();
    }

    @SneakyThrows
    @Transactional
    public Map<String, Long> importETLResult(String jobId, Date infoDate) {
        Map<String, Long> resultMap = new LinkedHashMap<>();
        FileImportStrategy importStrategy = fileImportStrategyFactory.getObject(DbSourceEnum.IMPALA);
        ImpalaImportConfig importConfig = impalaImportConfigRepository.findFirstByJobId(jobId);
        ScheduleJob scheduleJob = importConfig.getScheduleJob();
        Objects.requireNonNull(scheduleJob);
        JobExtraParam jobExtraParam = scheduleJob.getJobExtraParam();
        Objects.requireNonNull(jobExtraParam);
        String infoDateStr = DateUtils.formatDateToStr(DateUtils.DASHED_DATE_FORMAT, infoDate);
        File etlResultDir = ETLHelper.getResultOutputPath(rootPath, jobExtraParam.getRelativePath(), infoDate).toFile();
        List<File> fileList = FileOperationUtils.traversalAllFiles(etlResultDir, "csv.gz");

        for (File file : fileList) {
            final Date startTime = new Date();
            final String schemaName = "TEST";
            final String tableName = file.getName().replaceAll("_\\d{8,14}\\.csv\\.gz$", "");
            long effectedRecords = importStrategy.loadData(file, schemaName, tableName, false);
            resultMap.put(tableName, effectedRecords);
            log.info("[{}][{}] import records: {}", infoDateStr, tableName, effectedRecords);
            ImpalaImportLog importLog = new ImpalaImportLog();
            importLog.setJobId(jobId);
            importLog.setTargetTable(tableName);
            importLog.setDataDate(infoDate);
            importLog.setProcessTime(startTime);
            importLog.setProcessCompleteTime(new Date());
            importLog.setRecords(effectedRecords);
            importLog.setInfoDate(startTime);
            impalaImportLogRepository.save(importLog);
        }
        return resultMap;
    }

    @Transactional
    public Map<String, Long> importETLResult(Date infoDate) {
        List<ImpalaImportConfig> importCoinfgList = impalaImportConfigRepository.findByEnable(true);
        return importCoinfgList.stream().reduce(new LinkedHashMap<>(), (resultMap, config) -> {
            Map<String, Long> importResult = this.importETLResult(config.getJobId(), infoDate);
            resultMap.putAll(importResult);

            return resultMap;
        }, (o, n) -> n);
    }

    @Transactional
    public Map<String, Long> generateApply() {
        Map<String, Long> resultMap = new LinkedHashMap<>();

        return resultMap;
    }

    public Map<String, Object> updataDatasetStatsForEndDateTest() {
        Date updateTime = new Date();
        Map<String, Object> resultMap = new LinkedHashMap<>();
        //取得所有資料集資料(包含子表)
        List<Dataset> datasetList = datasetRepository.findAll().stream().filter(item -> Objects.nonNull(item.getDatasetStats()))
                .collect(Collectors.toList());
        JdbcTemplate jdbcTemplate = jdbcTemplateMap.get(DbSourceEnum.IMPALA).getJdbcTemplate();
        //將impala最新的檔案資料的infodata儲存在Dataset的子表，Dataset_stats表格的end_Date
        datasetList.forEach(item->{

            Dataset dataset = datasetRepository.findById(item.getSetId()).get();
            String minInfoDateSql = String.format("SELECT min(infodate) from %s",dataset.getLinkTable());
            String maxInfoDateSql = String.format("SELECT max(infodate) from %s",dataset.getLinkTable());
            String startDateStr = jdbcTemplate.queryForObject(minInfoDateSql,String.class);
            String endDateStr = jdbcTemplate.queryForObject(maxInfoDateSql,String.class);

            dataset.getDatasetStats().setStartDate(DateUtils.parseStrToDate("yyyy-MM-dd",startDateStr));
            dataset.getDatasetStats().setEndDate(DateUtils.parseStrToDate("yyyy-MM-dd",endDateStr));

            Map<String, Object> tatalFileInfo = new HashMap<>();
            String fileSizeStr = null;
            if(dataset.getLinkTable().contains("v_")){
                tatalFileInfo.put("Total",-1);
                tatalFileInfo.put("Size",-1);
                tatalFileInfo.put("#Rows",-1);
                fileSizeStr = tatalFileInfo.get("Size").toString();
            }else{
                String partitionsSql =  String.format("SHOW PARTITIONS %s",dataset.getLinkTable());
                List<Map<String,Object>> fileInfoList = jdbcTemplate.queryForList(partitionsSql)
                        .stream().filter(fileInfo->fileInfo.get("infodate").equals("Total")).collect(Collectors.toList());
                tatalFileInfo = fileInfoList.get(0);
                fileSizeStr = tatalFileInfo.get("Size").toString();
            }



            Long storedSize = transformToBytes(fileSizeStr);
            Long rowNum = Long.parseLong(tatalFileInfo.get("#Rows").toString());
            dataset.getDatasetStats().setRowNum(rowNum);
            dataset.getDatasetStats().setStoredSize(storedSize);
            dataset.getDatasetStats().setUpdateTime(updateTime);
            datasetRepository.save(dataset);
            resultMap.put(String.valueOf(dataset.getSetId()),dataset);

        });

        return resultMap;
    }

    public Long transformToBytes(String fileSizeStr) {

        String fileSizeStrForUpperCase = fileSizeStr.toUpperCase();
        Long bytes = Long.valueOf(0);
        if (fileSizeStrForUpperCase.contains("MB")) {
            double mb = Double.parseDouble(fileSizeStrForUpperCase.replace("MB", ""));
            bytes = (new Double(mb * 1024 * 1024 )).longValue();


        } else if (fileSizeStrForUpperCase.contains("GB")) {
            double gb = Double.parseDouble(fileSizeStrForUpperCase.replace("GB", ""));
            bytes = (new Double(gb * 1024 * 1024 * 1024)).longValue();

        } else if (fileSizeStrForUpperCase.contains("KB")){

            double kb = new Double(Double.parseDouble(fileSizeStrForUpperCase.replace("KB", ""))).longValue();
            bytes = (new Double(kb * 1024 )).longValue();

        }

        return bytes;
    }

}
