package com.example.test;

import com.example.component.ETLToolComponent;
import com.example.model.enums.DbSourceEnum;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.util.Map;

@SpringBootTest
@RunWith(SpringRunner.class)
public class GenerateApplyTest {

    @Autowired
    private ETLToolComponent etlToolComponent;

    @Autowired
    @Qualifier("jdbcTemplateMap")
    private Map<DbSourceEnum, NamedParameterJdbcTemplate> jdbcTemplateMap;

    @Test
    public void test(){

        JdbcTemplate jdbcTemplate = jdbcTemplateMap.get(DbSourceEnum.IMPALA).getJdbcTemplate();
        String table = "bus_route_kao";
        String minInfodateSql = String.format("SELECT min(infodate) from %s",table);
        jdbcTemplate.queryForObject(minInfodateSql,String.class);
        // etlToolComponent.generateApply();
    }

}
