package com.example.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.example.component.ETLToolComponent;
import com.example.dao.mysql.JobExtraParamRepository;
import com.example.dao.mysql.*;
import com.example.model.ProgramConstant;
import com.example.model.dto.etl.EncapsulationFile;
import com.example.model.dto.etl.ExtractResult;
import com.example.model.dto.etl.ResourceInfo;
import com.example.model.dto.source.other.RoadmgmtTtime;
import com.example.model.entity.mysql.*;
import com.example.service.interfaces.PushFileToImpalaService;
import com.example.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PushFileToImpalaServiceimpl implements PushFileToImpalaService {

    @Qualifier("mysqlJdbcTemplate")
    @Autowired
    private NamedParameterJdbcTemplate mysqlJdbcTemplate;

    @Qualifier("impalaJdbcTemplate")
    @Autowired
    private NamedParameterJdbcTemplate impalaJdbcTemplate;

    @Value("${root.path}")
    private String rootPath;

    @Autowired
    private FileInfoLogRepository fileInfoLogRepository;

    @Autowired
    private IntersectionEventDynamicHistoryRepository intersectionEventDynamicHistoryRepository;

    @Autowired
    private IntersectionStaticInfoMasterHistoryRepository intersectionStaticInfoMasterHistoryRepository;
    @Autowired
    private IntersectionStaticInfoDetailRepository intersectionStaticInfoDetailRepository;
    @Autowired
    private ScheduleJobRepository scheduleJobRepository;

    @Autowired
    private JobExtraParamRepository jobExtraParamRepository;

    @Autowired
    private RoadStaticInfoMasterHistoryRepository roadStaticInfoMasterHistoryRepository;

    @Autowired
    private RoadStaticInfoDetailHistoryRepository roadStaticInfoDetailHistoryRepository;

    @Autowired
    private IntersectionTurnDynamicMasterHistoryRepository intersectionTurnDynamicMasterHistoryRepository;
    @Autowired
    private IntersectionTurnDynamicDirectionHistoryRepository intersectionTurnDynamicDirectionHistoryRepository;
    @Autowired
    private IntersectionTurnDynamicTurnHistoryRepository intersectionTurnDynamicTurnHistoryRepository;

    @Autowired
    private IntersectionLaneDynamicMasterHistoryResppsitory intersectionLaneDynamicMasterHistoryResppsitory;

    @Autowired
    private IntersectionLaneDynamicDirectionHistoryRepository intersectionLaneDynamicDirectionHistoryRepository;

    @Autowired
    private IntersectionLaneDynamicLaneHistoryRepository intersectionLaneDynamicLaneHistoryRepository;

    @Autowired
    private RoadEventDynamicHistoryRepository roadEventDynamicHistoryRepository;

    @Autowired
    private RoadTurnDynamicMasterHistoryRepository roadTurnDynamicMasterHistoryRepository;

    @Autowired
    private RoadTurnDynamicDirectionHistoryRepository roadTurnDynamicDirectionHistoryRepository;
    @Autowired
    private RoadLaneDynamicMasterHistoryRepository roadLaneDynamicMasterHistoryRepository;
    @Autowired
    private RoadLaneDynamicDirectionHistoryRepository roadLaneDynamicDirectionHistoryRepository;
    @Autowired
    private RoadLaneDynamicLaneHistoryRespository roadLaneDynamicLaneHistoryRespository;
    @Autowired
    private ETLToolComponent etlToolComponent;

    @Autowired
    private DataDirectoryConfigRepository dataDirectoryConfigRepository;

    @Override
    public void pushRoadmgmtTtimeTheLastDay() {
        log.info("GVP資料集上架排程執行");
        try {

            Date updateTime = new Date();
            Map<String, Object> map = new HashMap<String, Object>();
            List<RoadmgmtTtime> roadmgmtTtimeList = mysqlJdbcTemplate.query("select * , recv_time as SrcUpdateTime " +
                    ",now() as UpdateTime " +
                    ",recv_time as  InfoTime " +
                    ",substr(cast(recv_time as varchar(100)),1,10) as InfoDate " +
                    "from dataset.roadmgmt_ttime " +
                    "where DATE(recv_time)=DATE_SUB(curdate(),INTERVAL 1 DAY)", map, (rs, rowNum) -> {
                RoadmgmtTtime roadmgmtTtime = new RoadmgmtTtime();
                roadmgmtTtime.setSeqId(rs.getString("seq_id"));
                roadmgmtTtime.setRoadId(rs.getString("road_id"));
                roadmgmtTtime.setRoadDesc(rs.getString("road_desc"));
                roadmgmtTtime.setLength(rs.getInt("length"));
                roadmgmtTtime.setTTime(rs.getInt("ttime"));
                roadmgmtTtime.setTTimeHistory(rs.getInt("ttime_history"));
                roadmgmtTtime.setTTimeDiffRatio(rs.getDouble("ttime_diff_ratio"));
                roadmgmtTtime.setSpeed(rs.getDouble("speed"));
                roadmgmtTtime.setSpeedTh1(rs.getDouble("speed_th1"));
                roadmgmtTtime.setSpeedTh2(rs.getDouble("speed_th2"));
                roadmgmtTtime.setMoeLevel(rs.getInt("moe_level"));
                roadmgmtTtime.setTti(rs.getDouble("tti"));
                roadmgmtTtime.setGroupId(rs.getString("group_id"));
                roadmgmtTtime.setCronId(rs.getString("cron_id"));
                roadmgmtTtime.setDatasourceId(rs.getString("datasource_id"));
                roadmgmtTtime.setDatasourceType(rs.getString("datasource_type"));
                String recvTimeStr = rs.getDate("recv_time") + " " + rs.getTime("recv_time");
                String srcUpdateTimeStr = rs.getDate("SrcUpdateTime") + " " + rs.getTime("SrcUpdateTime");
                String updateTimeStr = rs.getDate("UpdateTime") + " " + rs.getTime("UpdateTime");
                String infoTimeStr = rs.getDate("InfoTime") + " " + rs.getTime("InfoTime");
                String infoDateStr = rs.getDate("InfoTime") + " ";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-MM-dd");

                try {
                    roadmgmtTtime.setRecvTime(simpleDateFormat.parse(recvTimeStr));
                    roadmgmtTtime.setSrcUpdateTime(simpleDateFormat.parse(srcUpdateTimeStr));
                    roadmgmtTtime.setUpdateTime(simpleDateFormat.parse(updateTimeStr));
                    roadmgmtTtime.setInfoTime(simpleDateFormat.parse(infoTimeStr));
                    roadmgmtTtime.setInfoDate(simpleDateFormat2.parse(infoDateStr));
                } catch (ParseException e) {
                    log.error("GVP資料集上架排程異常");
                    log.error(e.getMessage(), e);
                    e.printStackTrace();
                }

                return roadmgmtTtime;
            });
            log.info("GVP資料即數量為{}", roadmgmtTtimeList.size());
            String json = JsonUtils.getMapper().writeValueAsString(roadmgmtTtimeList);

            ResourceInfo resourceInfo = ResourceInfo.builder()
                    .targetTable("dataset.roadmgmt_ttime")
                    .rootPath(rootPath)
                    .relativePath("/KHH/OTHER/ROADMGMT_TTIME")
                    .build();

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

            String infoDateStr = simpleDateFormat.format(roadmgmtTtimeList.get(0).getRecvTime());

            ExtractResult extractResult = ETLHelper.buildExtractResult(resourceInfo, json, simpleDateFormat.parse(infoDateStr), updateTime);
            ETLHelper.buildEncapsulationFile(extractResult, "dataset.roadmgmt_ttime", roadmgmtTtimeList);
            log.info("GVP資料集上架排程執行結束");
        } catch (Exception e) {
            log.error("GVP資料集上架排程異常");
            log.error(e.getMessage(), e);
            e.printStackTrace();
        }
    }

    @Override
    public void pushFileToFileServer() {

        List<DataDirectoryConfig> dataDirectoryConfigList = dataDirectoryConfigRepository.findAll().stream().collect(Collectors.collectingAndThen(
                Collectors.toCollection(()-> new TreeSet<>(Comparator.comparing(DataDirectoryConfig::getJobId))),ArrayList::new));

        dataDirectoryConfigList.forEach(s->{
            this.pushFileToFileServer(s.getJobId());

        });


    }



    public void pushFileToFileServer(String jobId) {
        JobExtraParam jobExtraParam = jobExtraParamRepository.findByJobId(jobId);
        ScheduleJob scheduleJob = scheduleJobRepository.findByJobId(jobId);

        Date updateTime = new Date();
        Date lastDay = DateUtils.addDays(updateTime, -1);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat simpleDateFormatNoUnderLine = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
        SimpleDateFormat monthFormat = new SimpleDateFormat("yyyy-MM");
        String lastDayStr = simpleDateFormat.format(lastDay);
        String yearStr = yearFormat.format(lastDay);
        String monthStr = monthFormat.format(lastDay);
        String dateFormatNoUnderLine = simpleDateFormatNoUnderLine.format(lastDay);


        List<DataDirectoryConfig> dataDirectoryConfigList = dataDirectoryConfigRepository.findByJobId(jobId);

        dataDirectoryConfigList.forEach(s->{

            String filePath = String.format("%s%s/%s/%s/%s/%s_%s.csv.gz", rootPath, jobExtraParam.getRelativePath(), yearStr, monthStr,
                    lastDayStr, s.getLinkTable().replace("dataset.", ""), dateFormatNoUnderLine);

            File file = new File(filePath);

                try {
                    FileInfoLog fileInfoLog = new FileInfoLog();
                    String hdfsPath = String.format("/user/hdfs/TEST/FileServer%s/%s/%s", s.getFileServerRelativePath(),yearStr, monthStr);

                    System.out.println(String.format("%s/%s/%s", s.getFileServerRelativePath(),yearStr, monthStr));
                    fileInfoLog.setRelativePath(String.format("%s/%s/%s", s.getFileServerRelativePath(),yearStr, monthStr));
                    String linkTable = s.getLinkTable().replace("dataset.","");
                    String fileName = String.format("%s_%s.csv.gz", linkTable,dateFormatNoUnderLine);
                    fileInfoLog.setFileName(fileName);
                    fileInfoLog.setVersionId(1);
                    fileInfoLog.setFileSize(file.length());
                    String md5 = DigestUtils.md5DigestAsHex(fileName.getBytes(StandardCharsets.UTF_8));
                    fileInfoLog.setMd5(md5);
                    fileInfoLog.setCreateTime(updateTime);
                    fileInfoLog.setUpdateTime(null);
                    fileInfoLog.setStatus(1);

                    String pushPath = HDFSUtils.upload(filePath, hdfsPath);

                    fileInfoLogRepository.save(fileInfoLog);
                    log.info("推送的檔案: {}", filePath);
                    log.info("上傳路徑: {}", pushPath);
                    log.info("{}(前一天)執行已上傳",scheduleJob.getJobName());

                } catch (Exception e) {
                    log.error("{}(前一天)執行上傳失敗",scheduleJob.getJobName());
                    log.error(e.getMessage());
                    e.printStackTrace();

                }


        });



    }

    public ResourceInfo getResourceInfo(JobExtraParam jobExtraParam) throws ClassNotFoundException {
        ResourceInfo resourceInfo = ResourceInfo.builder().rootPath(rootPath)
                .relativePath(jobExtraParam.getRelativePath())
                .modelClass(Class.forName(jobExtraParam.getClassName()))
                .targetTable(jobExtraParam.getLinkTable())
                .build();

        return resourceInfo;
    }

    ;

    public void transform(ExtractResult extractResult, ResourceInfo resourceInfo) {
        // Step 1: Serialize to related model
        long startTime = System.currentTimeMillis();
        Class<?> modelClass = resourceInfo.getModelClass();
        List<File> resources = extractResult.getResources();
        List<Serializable> resultList = resources.stream().map(resource -> {
            String content = FileOperationUtils.extractContent(resource);

            List<?> beanList = JsonUtils.toBeanList(content, modelClass);
            // Step 2: Transfer result and map to related model
            try {
                Field srcUpdateTimeField = modelClass.getDeclaredField(ProgramConstant.SRCUPDATETIME_FIELD_NAME);
                Field updateTimeField = modelClass.getDeclaredField(ProgramConstant.UPDATETIME_FIELD_NAME);
                srcUpdateTimeField.setAccessible(true);
                updateTimeField.setAccessible(true);
                return beanList.stream().map(bean -> {
                    try {
                        // Set srcUpdateTime
                        srcUpdateTimeField.set(bean, extractResult.getSrcUpdateTime());
                        // Set updateTime
                        updateTimeField.set(bean, extractResult.getUpdateTime());
                        // Self set value with annotation @AssignFrom
                        ClassUtils.selfAssign(bean);
                    } catch (ReflectiveOperationException e) {
                        log.error(e.getMessage(), e);
                    }

                    return (Serializable) bean;
                }).distinct().collect(Collectors.toList());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }

            return new ArrayList<Serializable>();
        }).flatMap(List::stream).distinct().collect(Collectors.toList());

        EncapsulationFile encapsulationFile = ETLHelper.buildEncapsulationFile(extractResult,
                resourceInfo.getTargetTable(), resultList);
        log.info("Extract result transform spent {}ms", System.currentTimeMillis() - startTime);

    }
}
