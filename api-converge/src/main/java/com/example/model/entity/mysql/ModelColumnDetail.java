package com.example.model.entity.mysql;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.example.model.entity.mysql.pk.ModelColumnDetailPK;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@IdClass(ModelColumnDetailPK.class)
@Table(schema = "web", name = "model_column_detail")
//@ToString(exclude = "subColumnDetails")
@NamedEntityGraph(
		name = "ModelColumnDetail.fetchAll",
		attributeNodes = {
				@NamedAttributeNode(value = "subColumnDetails")
		}
)
@JsonInclude(JsonInclude.Include.NON_NULL)
public @Data class ModelColumnDetail implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "d_belong_model")
	private String dBelongModel;
	
	@Id
	private String cname;

	@Column(name = "outbound_name")
	private String outboundName;

	@Column(name = "d_type")
	private String dType;
	
	@Column(name = "d_link_model")
	private String dLinkModel;

	@Column(name = "d_length")
	private Integer dLength;

	@Column(name = "d_check")
	private String dCheck;

	@Column(name = "d_desc")
	private String dDesc;

	@Column(name = "is_null")
	private boolean isNull;

	@Column(name = "is_custom")
	private boolean isCustom;

	@Column(name = "is_outbound")
	private boolean isOutbound;

	@Column(name = "privacy_mode")
	private boolean privacyMode;

	@Column(name = "order_no")
	private int orderNo;

@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
@JoinColumn(name = "d_belong_model", referencedColumnName = "d_link_model", insertable = false, updatable = false)
@OrderBy(value = "orderNo")
	private List<ModelColumnDetail> subColumnDetails;

}
