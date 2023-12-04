package com.example.dao;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import com.example.model.entity.base.AbstractScheduleJob;

@NoRepositoryBean
public interface GenericScheduleJobRepository<T extends AbstractScheduleJob> extends JpaRepository<T, String> {

	@Override
	@EntityGraph(value = "JobNotifyUsers.Fetch", type = EntityGraphType.FETCH)
	public List<T> findAll();

	@EntityGraph(value = "JobNotifyUsers.Fetch", type = EntityGraphType.FETCH)
	public T findByJobId(String jobId);
	
	@EntityGraph(value = "JobNotifyUsers.Fetch", type = EntityGraphType.FETCH)
	public List<T> findByJobStrategy(String jobStrategy);

	@EntityGraph(value = "JobNotifyUsers.Fetch", type = EntityGraphType.FETCH)
	public List<T> findByJobGroup(String jobGroup);

}
