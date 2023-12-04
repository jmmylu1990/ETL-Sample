package com.example.model.entity.mysql;

import com.example.model.entity.mysql.pk.FileInfoLogKey;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(schema = "web",name = "file_info_log")
@IdClass(FileInfoLogKey.class)
public @Data class FileInfoLog implements Serializable {

    @Id
    @Column(name = "relative_path")
    private String relativePath;
    @Id
    @Column(name = "file_name")
    private String fileName;
    @Id
    @Column(name = "version_id")
    private Integer versionId;
    @Column(name = "file_size")
    private Long fileSize;
    private String md5;
    @Column(name = "create_time")
    private Date createTime;
    @Column(name = "update_time")
    private Date updateTime;
    private Integer status;

}
