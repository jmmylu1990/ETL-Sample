package com.example.dao.mysql;

import org.springframework.stereotype.Repository;

import com.example.dao.GenericScheduleLogRepository;
import com.example.model.entity.mysql.ScheduleLog;

@Repository
public interface ScheduleLogRepository extends GenericScheduleLogRepository<ScheduleLog> {

}
