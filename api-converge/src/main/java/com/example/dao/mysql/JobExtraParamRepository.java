package com.example.dao.mysql;

import com.example.model.entity.mysql.JobExtraParam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobExtraParamRepository extends JpaRepository<JobExtraParam,String> {
    public JobExtraParam findByJobId(String jobId);

}
