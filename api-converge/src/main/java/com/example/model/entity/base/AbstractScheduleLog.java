package com.example.model.entity.base;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.example.model.entity.base.pk.ScheduleLogPK;
import com.example.model.enums.ETLResultEnum;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Entity
@IdClass(ScheduleLogPK.class)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract @Data class AbstractScheduleLog implements Serializable {
	
	private static final long serialVersionUID = 1L;

	/** 排程編號 **/
	@Id
	@Column(name = "job_id", length = 50, nullable = false)
	protected String jobId;

	@Id
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "process_time")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	protected Date processTime;

	@Column(nullable = false)
	@Enumerated(EnumType.ORDINAL)
	protected ETLResultEnum result;

	@Column(name = "refire_flag", nullable = false)
	protected int refireFlag;

	@Column(name = "callback_msg", length = 1000)
	protected String callbackMsg;
}