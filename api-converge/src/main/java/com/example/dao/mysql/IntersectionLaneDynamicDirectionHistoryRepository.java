package com.example.dao.mysql;

import com.example.model.entity.mysql.IntersectionLaneDynamicDirectionHistory;
import com.example.model.entity.mysql.pk.IntersectionLaneDynamicDirectionHistoryKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IntersectionLaneDynamicDirectionHistoryRepository extends JpaRepository<IntersectionLaneDynamicDirectionHistory, IntersectionLaneDynamicDirectionHistoryKey> {
}
