package com.example.model.entity.base;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.OneToMany;

import org.hibernate.annotations.BatchSize;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.example.model.entity.mysql.JobNotifyUser;
import com.example.model.enums.JobResultEnum;
import com.example.model.enums.StateEnum;

import lombok.Data;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@NamedEntityGraph(name = "JobNotifyUsers.Fetch", attributeNodes = @NamedAttributeNode("jobNotifyUsers"))
public abstract @Data class AbstractScheduleJob implements Serializable {
	
	protected static final long serialVersionUID = 1L;

	/** 排程編號 **/
	@Id
	@Column(name = "job_id", length = 50, nullable = false)
	protected String jobId;

	@Column(name = "job_strategy")
	protected String jobStrategy;

	/** 排程名稱 **/
	@Column(name = "job_name", nullable = false)
	protected String jobName;

	/** 排程描述 **/
	@Column(name = "job_desc")
	protected String jobDesc;

	/** 排程分組 **/
	@Column(name = "job_group", nullable = false)
	protected String jobGroup;

	/** 排程狀態 0禁用 1啟用 2刪除 **/
	@Column(name = "job_status")
	@Enumerated(EnumType.ORDINAL)
	protected StateEnum jobStatus;

	/** 排程最後執行時間 **/
	@Column(name = "last_fire_time")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	protected Date lastFireTime;

	/** 排程最後完成時間 **/
	@Column(name = "last_complete_time")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	protected Date lastCompleteTime;

	/** 排程最後執行結果 0失敗 1成功 **/
	@Column(name = "last_result")
	@Enumerated(EnumType.ORDINAL)
	protected JobResultEnum lastResult;

	/** 排程下次執行時間 **/
	@Column(name = "next_fire_time")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	protected Date nextFireTime;

	/** 排程運行時間Cron表達式 **/
	@Column(name = "[cron]")
	protected String cronExpression;

	/** 重新執行次數 **/
	@Column(name = "refire_max_count")
	protected int refireMaxCount;

	/** 重新執行區間 **/
	@Column(name = "refire_interval")
	protected int refireInterval;

	/** 錯誤累積次數 **/
	@Column(name = "error_accumulation")
	protected int errorAccumulation;
	
	// bi-directional many-to-one association to JobNotifyUser
	@BatchSize(size = 20)
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
	@JoinColumn(name = "job_id", nullable = true)
	protected List<JobNotifyUser> jobNotifyUsers;

	public JobNotifyUser addJobNotifyUser(JobNotifyUser jobNotifyUser) {
		getJobNotifyUsers().add(jobNotifyUser);
		jobNotifyUser.setScheduleJob(this);

		return jobNotifyUser;
	}

	public JobNotifyUser removeJobNotifyUser(JobNotifyUser jobNotifyUser) {
		getJobNotifyUsers().remove(jobNotifyUser);
		jobNotifyUser.setScheduleJob(null);

		return jobNotifyUser;
	}
	
}