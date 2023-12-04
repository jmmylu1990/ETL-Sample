package com.example.dao.mysql;

import com.example.model.entity.mysql.IntersectionStaticInfoMasterHistory;
import com.example.model.entity.mysql.IntersectionStaticInfoMasterHistoryKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.Embeddable;

@Repository
public interface IntersectionStaticInfoMasterHistoryRepository extends JpaRepository<IntersectionStaticInfoMasterHistory, IntersectionStaticInfoMasterHistoryKey> {
}
