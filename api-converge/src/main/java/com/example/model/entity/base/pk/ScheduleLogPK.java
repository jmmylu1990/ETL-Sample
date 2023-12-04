package com.example.model.entity.base.pk;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Embeddable
public @Data class ScheduleLogPK implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Column(name = "job_id", length = 50)
	private String jobId;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "process_time")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date processTime;

}
