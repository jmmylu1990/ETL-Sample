package com.example.dao.mysql;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.example.dao.GenericScheduleJobRepository;
import com.example.model.entity.mysql.ScheduleJob;

@Repository
public interface ScheduleJobRepository extends GenericScheduleJobRepository<ScheduleJob> {

	public List<ScheduleJob> findByJobGroup(String jobGroup);
}
