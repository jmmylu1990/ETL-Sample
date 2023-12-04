package com.example.model.entity.mysql.pk;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class JobNotifyUserPK implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Column(name = "job_id", length = 50)
	private String jobId;

	@Column(length = 100)
	private String mail;

	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		JobNotifyUserPK jobNotifyUserPK = (JobNotifyUserPK) o;
		return Objects.equals(jobId, jobNotifyUserPK.jobId) &&
				Objects.equals(mail, jobNotifyUserPK.mail);
	}

	@Override
	public int hashCode() {
		return Objects.hash(jobId, mail);
	}
}
