package com.example.test;

import com.example.dao.mysql.DatasetRepository;
import com.example.model.entity.mysql.Dataset;
import com.example.model.enums.DbSourceEnum;
import com.example.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class UpdataDatasetStatsForEndDateTest {

    @Autowired
    private DatasetRepository datasetRepository;

    @Autowired
    @Qualifier("jdbcTemplateMap")
    private Map<DbSourceEnum, NamedParameterJdbcTemplate> jdbcTemplateMap;

    @Test
    public void run() {
            Date updateTime = new Date();
        Map<String, Object> resultMap = new LinkedHashMap<>();
        //取得所有資料集資料(包含子表)
        List<Dataset> datasetList = datasetRepository.findAll().stream().filter(item -> Objects.nonNull(item.getDatasetStats()))
                .collect(Collectors.toList());
        JdbcTemplate jdbcTemplate = jdbcTemplateMap.get(DbSourceEnum.IMPALA).getJdbcTemplate();
        //將impala最新的檔案資料的infodata儲存在Dataset的子表，Dataset_stats表格的end_Date
        datasetList.forEach(item->{

            Dataset dataset = datasetRepository.findById(item.getSetId()).get();
            String maxInfoDateSql = String.format("SELECT max(infodate) from %s",dataset.getLinkTable());
            String endDateStr = jdbcTemplate.queryForObject(maxInfoDateSql,String.class);
            dataset.getDatasetStats().setEndDate(DateUtils.parseStrToDate("yyyy-MM-dd",endDateStr));
            String partitionsSql =  String.format("SHOW PARTITIONS %s",dataset.getLinkTable());
            List<Map<String,Object>> fileInfoList = jdbcTemplate.queryForList(partitionsSql)
                    .stream().filter(fileInfo->fileInfo.get("infodate").equals("Total")).collect(Collectors.toList());
            Map<String,Object> tatalFileInfo = fileInfoList.get(0);
            Long storedSize = transformToBytes(tatalFileInfo.get("Size").toString());
            Long rowNum = Long.parseLong(tatalFileInfo.get("#Rows").toString());
            dataset.getDatasetStats().setRowNum(rowNum);
            dataset.getDatasetStats().setStoredSize(storedSize);
            dataset.getDatasetStats().setUpdateTime(updateTime);
            datasetRepository.save(dataset);
            resultMap.put(String.valueOf(dataset.getSetId()),dataset);

        });

    }

    public Long transformToBytes(String fileSizeStr) {

        String fileSizeStrForUpperCase = fileSizeStr.toUpperCase();
        Long bytes = null;
        if (fileSizeStrForUpperCase.contains("MB")) {
            double mb = Double.parseDouble(fileSizeStrForUpperCase.replace("MB", ""));
            bytes = (new Double(mb * 1024 * 1024 )).longValue();


        } else if (fileSizeStrForUpperCase.contains("GB")) {
            double gb = Double.parseDouble(fileSizeStrForUpperCase.replace("GB", ""));
            bytes = (new Double(gb * 1024 * 1024 * 1024)).longValue();

        } else {

            double kb = new Double(Double.parseDouble(fileSizeStrForUpperCase.replace("KB", ""))).longValue();
            bytes = (new Double(kb * 1024 )).longValue();

        }

        return bytes;
    }

}
