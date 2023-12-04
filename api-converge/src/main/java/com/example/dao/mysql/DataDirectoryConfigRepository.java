package com.example.dao.mysql;

import com.example.model.entity.mysql.DataDirectoryConfig;
import com.example.model.entity.mysql.pk.DataDirectoryConfigKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DataDirectoryConfigRepository extends JpaRepository<DataDirectoryConfig, DataDirectoryConfigKey>{


   public List<DataDirectoryConfig> findByJobId(String jobId);
}
