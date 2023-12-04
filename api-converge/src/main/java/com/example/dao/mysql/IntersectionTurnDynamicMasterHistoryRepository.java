package com.example.dao.mysql;

import com.example.model.entity.mysql.IntersectionTurnDynamicMaster;
import com.example.model.entity.mysql.IntersectionTurnDynamicMasterHistory;
import com.example.model.entity.mysql.pk.IntersectionTurnDynamicMasterHistoryKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IntersectionTurnDynamicMasterHistoryRepository extends JpaRepository<IntersectionTurnDynamicMasterHistory, IntersectionTurnDynamicMasterHistoryKey> {
}
