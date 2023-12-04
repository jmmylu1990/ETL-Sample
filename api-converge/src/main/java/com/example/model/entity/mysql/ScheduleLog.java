package com.example.model.entity.mysql;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.example.model.entity.base.AbstractScheduleLog;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(schema = "web", name = "schedule_log")
@EqualsAndHashCode(callSuper = false)
public @Data class ScheduleLog extends AbstractScheduleLog {

	private static final long serialVersionUID = 1L;

	@Column(name = "src_data_count", nullable = false)
	private long srcDataCount;

	@Column(name = "import_data_count", nullable = false)
	private long importDataCount;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "import_time")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date importTime;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "complete_time")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date completeTime;
	
	@Temporal(TemporalType.DATE)
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	private Date infoDate;

//	@JsonIgnore
//	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//	@JoinColumn(name = "job_id", insertable = false, updatable = false)
//	private ScheduleJob scheduleJob;
}
