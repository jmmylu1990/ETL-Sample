package com.example.model.entity.mysql.pk;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;


public @Data class FileInfoLogKey implements Serializable {

    @Column(name = "relative_path")
    private String relativePath;
    @Column(name = "file_name")
    private String fileName;
    @Column(name = "version_id")
    private Integer versionId;

}
