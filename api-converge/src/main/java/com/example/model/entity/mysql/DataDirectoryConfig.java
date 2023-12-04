package com.example.model.entity.mysql;

import com.example.model.entity.mysql.pk.DataDirectoryConfigKey;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(schema = "web", name = "data_directory_config")
@IdClass(DataDirectoryConfigKey.class)
public @Data
class DataDirectoryConfig implements Serializable {

    @Id
    @Column(name="job_id")
    private String jobId;
    @Id
    @Column(name="link_table")
    private String linkTable;
    @Column(name="file_server_relative_path")
    private String fileServerRelativePath;

    @Column(name="file_information")
    private String fileInformation;

}
