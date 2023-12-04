package com.example.model.entity.mysql.pk;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Data;

@Embeddable
public @Data class ImpalaImportConfigPK implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Column(name = "job_id", length = 50)
	private String jobId;

	@Column(name = "target_table")
	private String targetTable;

}
