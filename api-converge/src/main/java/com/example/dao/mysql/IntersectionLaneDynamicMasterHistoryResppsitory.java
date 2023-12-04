package com.example.dao.mysql;

import com.example.model.entity.mysql.IntersectionLaneDynamicMasterHistory;
import com.example.model.entity.mysql.pk.IntersectionLaneDynamicMasterHistoryKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IntersectionLaneDynamicMasterHistoryResppsitory extends JpaRepository<IntersectionLaneDynamicMasterHistory, IntersectionLaneDynamicMasterHistoryKey> {
}
