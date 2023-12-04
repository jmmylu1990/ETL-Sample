package com.example.dao.mysql;

import com.example.model.entity.mysql.IntersectionTurnDynamicDirectionHistory;
import com.example.model.entity.mysql.pk.IntersectionTurnDynamicDirectionHistoryKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IntersectionTurnDynamicDirectionHistoryRepository extends JpaRepository<IntersectionTurnDynamicDirectionHistory, IntersectionTurnDynamicDirectionHistoryKey> {
}
