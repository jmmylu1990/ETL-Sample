package com.example.dao.mysql;

import com.example.model.entity.mysql.FileInfoLog;
import com.example.model.entity.mysql.pk.FileInfoLogKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileInfoLogRepository extends JpaRepository<FileInfoLog, FileInfoLogKey> {
}
