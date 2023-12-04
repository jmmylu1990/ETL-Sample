package com.example.model.entity.mysql;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.example.converter.StringListConverter;
import lombok.Data;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "dataset")
@NamedEntityGraph(name = "Dataset.fetchAll",
		attributeNodes = {
//		@NamedAttributeNode(value = "datasetProvider"),
//		@NamedAttributeNode(value = "datasetCategory"),
//		@NamedAttributeNode(value = "datasetType"),
				@NamedAttributeNode(value = "datasetStats")
		}
)
public @Data class Dataset implements  Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "set_id")
	private int setId;

	private String name;

	@Column(name = "set_desc")
	private String setDesc;

	@Column(name = "keyword")
	@Convert(converter = StringListConverter.class)
	private List<String> keywords;


	@Column(name = "provider_id")
	private int providerId;

	@Column(name = "cat_id")
	private int catId;

	@Column(name = "type_id")
	private int typeId;


	private int frequence;

	@JsonIgnore
	@Column(name = "db_source")
	private String dbSource;

	@JsonIgnore
	@Column(name = "link_table")
	private String linkTable;

	@Column(name = "link_model")
	private String linkModel;
	@Column(name = "is_auto_approve")
	private Integer isAutoApprove;

	@Column(name = "create_by")
	private String createBy;
	@Column(name = "update_by")
	private String updateBy;

	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@Column(name = "create_time")
	private Date createTime;

	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@Column(name = "update_time")
	private Date updateTime;

	private Integer enable;

	@Temporal(TemporalType.DATE)
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	@Column(name = "infodate")
	private Date infoDate;

	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "set_id", insertable = false, updatable = false)
	private DatasetStats datasetStats;

//	@Formula("(SELECT t.desc FROM web.dataset_category AS t WHERE t.id = cat_id)")
//	private String catName;
//
//	@Formula("(SELECT t.desc FROM web.dataset_type AS t WHERE t.id = type_id)")
//	private String typeName;
//
//	@Formula("(SELECT t.desc FROM web.dataset_provider AS t WHERE t.id = provider_id)")
//	private String providerName;

//	@Formula("(SELECT t.\"desc\" FROM web.dataset_category AS t WHERE t.id = cat_id)")
//	private String catName;

}
