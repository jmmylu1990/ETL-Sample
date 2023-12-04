package com.example.model.entity.mysql;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.example.model.entity.base.AbstractScheduleJob;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@DynamicUpdate
@Table(schema = "web", name = "schedule_job")
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public @Data class ScheduleJob extends AbstractScheduleJob {

	private static final long serialVersionUID = 1L;
	
	@OneToOne(fetch = FetchType.EAGER, optional = true)
	@NotFound(action = NotFoundAction.IGNORE)
	@JoinColumn(name = "job_id", nullable = true, insertable = false, updatable = false)
	private JobExtraParam jobExtraParam;
	
}
