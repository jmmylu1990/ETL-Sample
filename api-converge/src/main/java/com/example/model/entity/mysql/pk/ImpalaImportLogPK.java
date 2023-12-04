package com.example.model.entity.mysql.pk;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;

@Embeddable
public @Data class ImpalaImportLogPK implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Column(name = "job_id", length = 50)
	private String jobId;

	@Column(name = "process_time")
	@Temporal(TemporalType.TIMESTAMP)
	private Date processTime;

}
