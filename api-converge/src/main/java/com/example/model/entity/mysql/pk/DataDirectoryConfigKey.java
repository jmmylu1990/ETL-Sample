package com.example.model.entity.mysql.pk;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Id;
import java.io.Serializable;

@Embeddable
public @Data class DataDirectoryConfigKey implements Serializable {
    @Id
    @Column(name="job_id")
    private String jobId;
    @Id
    @Column(name="link_table")
    private String linkTable;
    @Column(name="file_server_relative_path")
    private String fileServerRelativePath;
}
