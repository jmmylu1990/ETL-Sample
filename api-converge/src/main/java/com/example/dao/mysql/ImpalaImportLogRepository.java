package com.example.dao.mysql;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.model.entity.mysql.ImpalaImportLog;
import com.example.model.entity.mysql.pk.ImpalaImportLogPK;

@Repository
public interface ImpalaImportLogRepository extends JpaRepository<ImpalaImportLog, ImpalaImportLogPK> {

}
