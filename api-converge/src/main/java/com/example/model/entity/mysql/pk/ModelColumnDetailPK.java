package com.example.model.entity.mysql.pk;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public @Data class ModelColumnDetailPK implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Column(name = "d_belong_model")
	private String dBelongModel;
	
	private String cname;

}