package com.example.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import com.example.model.entity.base.AbstractScheduleLog;
import com.example.model.entity.base.pk.ScheduleLogPK;

@NoRepositoryBean
public interface GenericScheduleLogRepository<T extends AbstractScheduleLog> extends JpaRepository<T, ScheduleLogPK> {

}
