package com.example.model.entity.mysql;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.example.model.entity.base.AbstractScheduleJob;
import com.example.model.entity.mysql.pk.JobNotifyUserPK;

import lombok.Data;

@Entity
@IdClass(JobNotifyUserPK.class)
@Table(schema = "web", name = "job_notify_user")
@NamedQuery(name = "JobNotifyUser.findAll", query = "SELECT j FROM JobNotifyUser j")
public @Data class JobNotifyUser implements Serializable {

	private static final long serialVersionUID = 1L;
		
	@Id
	@Column(name = "job_id", length = 50, nullable = false, unique = true)
	private String jobId;

	@Column(length = 60)
	private String aname;

	@Id
	@Column(name = "mail", length = 100, nullable = false, unique = true)
	private String mail;

	@Column(name = "notify_cond", length = 45, nullable = false)
	private String notifyCond;

	@Column(nullable = false)
	private boolean enable;
	
	// bi-directional many-to-one association to JobNotifyUser
	@JsonIgnore
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "job_id", insertable = false, updatable = false)
	private AbstractScheduleJob scheduleJob;

}
