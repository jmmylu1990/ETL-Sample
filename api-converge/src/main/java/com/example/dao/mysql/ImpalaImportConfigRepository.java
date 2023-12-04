package com.example.dao.mysql;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.model.entity.mysql.ImpalaImportConfig;
import com.example.model.entity.mysql.pk.ImpalaImportConfigPK;

@Repository
public interface ImpalaImportConfigRepository extends JpaRepository<ImpalaImportConfig, ImpalaImportConfigPK> {

	public ImpalaImportConfig findFirstByJobId(String jobId);

	public List<ImpalaImportConfig> findByEnable(boolean enable);
	
}
