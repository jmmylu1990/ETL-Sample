package com.example.model.entity.mysql;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(catalog = "dataset", name = "tomtom_request_point")
public @Data class TomtomRequestPoint implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@Id
	private Integer seq;
	@Column(name="left_up_lat")
	private Double leftUpLat;
	@Column(name="left_up_lon")
	private Double leftUpLon;
	@Column(name="right_up_lat")
	private Double rightUpLat;
	@Column(name="right_up_lon")
	private Double rightUpLon;
	@Column(name="left_down_lat")
	private Double leftDownLat;
	@Column(name="left_down_lon")
	private Double leftDownLon;
	@Column(name="right_down_lat")
	private Double rightDownLat;
	@Column(name="right_down_lon")
	private Double rightDownLon;

}
