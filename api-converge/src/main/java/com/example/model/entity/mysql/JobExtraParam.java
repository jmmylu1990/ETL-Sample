package com.example.model.entity.mysql;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(schema = "web", name = "job_extra_param")
public @Data class JobExtraParam implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "job_id", length = 50, nullable = false)
	private String jobId;

	@Column(name = "class_name", length = 100)
	private String className;
	
	@Column(name = "resource_url", length = 500)
	private String resourceUrl;
	
	@Column(name = "db_sources", length = 50)
	private String dbSources;
	
	@Column(name = "link_table", length = 100)
	private String linkTable;

	@Column(name = "source_type", length = 40)
	private String sourceType;

	@Column(name = "relative_path", length = 100)
	private String relativePath;
	
	@Column(name = "clear_first", nullable = false)
	private boolean clearFirst;

	@OneToOne(mappedBy = "jobExtraParam", fetch = FetchType.LAZY, cascade = CascadeType.MERGE, optional = true)
	private ScheduleJob scheduleJob;
	
}
