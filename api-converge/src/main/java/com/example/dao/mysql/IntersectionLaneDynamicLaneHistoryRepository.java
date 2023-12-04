package com.example.dao.mysql;

import com.example.model.entity.mysql.IntersectionLaneDynamicLaneHistory;
import com.example.model.entity.mysql.pk.IntersectionLaneDynamicLaneHistoryKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IntersectionLaneDynamicLaneHistoryRepository extends JpaRepository<IntersectionLaneDynamicLaneHistory, IntersectionLaneDynamicLaneHistoryKey> {
}
