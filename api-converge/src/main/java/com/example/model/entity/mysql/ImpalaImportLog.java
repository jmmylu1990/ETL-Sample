package com.example.model.entity.mysql;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.example.model.entity.mysql.pk.ImpalaImportLogPK;

import lombok.Data;

@Entity
@IdClass(ImpalaImportLogPK.class)
@Table(schema = "web", name = "impala_import_log")
public @Data class ImpalaImportLog implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "job_id")
	private String jobId;
	
	@Column(name = "target_table")
	private String targetTable;
	
	@Column(name = "data_date")
	@Temporal(TemporalType.DATE)
	private Date dataDate;

	private long records;
	
	@Id
	@Column(name = "process_time")
	@Temporal(TemporalType.TIMESTAMP)
	private Date processTime;
	
	@Column(name = "process_complete_time")
	@Temporal(TemporalType.TIMESTAMP)
	private Date processCompleteTime;
	
	@Temporal(TemporalType.DATE)
	private Date infoDate;
}
