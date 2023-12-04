package com.example.test;

import com.example.dao.GenericScheduleJobRepository;
import com.example.dao.mysql.ScheduleJobRepository;
import com.example.model.entity.base.AbstractScheduleJob;
import com.example.model.entity.mysql.JobExtraParam;
import com.example.model.entity.mysql.ScheduleJob;
import org.junit.jupiter.api.Test;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@SpringBootTest
public class test {

    @Qualifier("mysqlJdbcTemplate")
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    private ScheduleJobRepository scheduleJobRepository;

    @Test
    public void test(){
       List<Map<String, Object>> scheduleJobs = namedParameterJdbcTemplate.queryForList("select * from web.schedule_job",new HashMap<>());

       Map<String, Object> scheduleJob =  scheduleJobs.stream().filter(s->s.values().contains("7d6f412e20e89dc8d39ba1442be5f4ba")).findFirst().get();

        Map<String, Object>  map = new HashMap<String, Object>();
        map.put("jobId",scheduleJob.get("job_id"));
        List<JobExtraParam> jobExtraParama = namedParameterJdbcTemplate.query("select * from web.job_extra_param where job_id = :jobId", map, new RowMapper<JobExtraParam>() {

            @Override
            public JobExtraParam mapRow(ResultSet rs, int rowNum) throws SQLException {
                JobExtraParam jobExtraParam = new JobExtraParam();
                jobExtraParam.setRelativePath(rs.getString("relative_path"));
                return jobExtraParam;
            }
        });

    }

    @Test
    public void test2(){

        ScheduleJob scheduleJob = scheduleJobRepository.findAll().get(0);

    }

}
