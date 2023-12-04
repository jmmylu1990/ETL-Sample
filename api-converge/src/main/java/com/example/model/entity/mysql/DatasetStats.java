package com.example.model.entity.mysql;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(schema = "web",name = "dataset_stats")
public @Data class DatasetStats implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@JsonIgnore
	@Column(name = "set_id", nullable = false)
	private int setId;

	@Column(name = "start_date")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	private Date startDate;

	@Column(name = "end_date")
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	private Date endDate;
	
	@Column(name = "row_num")
	private long rowNum;

	@Column(name = "stored_size")
	private long storedSize;

	@Column(name = "preview_count")
	private long previewCount;

	@Column(name = "download_count")
	private long downloadCount;

	@Column(name = "apply_count")
	private long applyCount;

	@Column(name = "update_time")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date updateTime;
	
}
