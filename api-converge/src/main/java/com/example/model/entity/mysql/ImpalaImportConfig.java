package com.example.model.entity.mysql;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.example.model.entity.mysql.pk.ImpalaImportConfigPK;

import lombok.Data;

@Entity
@IdClass(ImpalaImportConfigPK.class)
@Table(schema = "web", name = "impala_import_config")
public @Data class ImpalaImportConfig implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "job_id")
	private String jobId;

	@Column(name = "hdfs_path")
	private String hdfsPath;
	
	@Id
	@Column(name = "target_table")
	private String targetTable;

	@Column(name = "sql_key")
	private String sqlKey;
	
	private boolean enable;
	
	@OneToOne(fetch = FetchType.EAGER, optional = true)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "job_id", nullable = true, insertable = false, updatable = false)
	private ScheduleJob scheduleJob;
	
}
