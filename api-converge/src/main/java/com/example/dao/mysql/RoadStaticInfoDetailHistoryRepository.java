package com.example.dao.mysql;

import com.example.model.entity.mysql.RoadStaticInfoDetailHistory;
import com.example.model.entity.mysql.pk.RoadStaticInfoDetailHistoryKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoadStaticInfoDetailHistoryRepository extends JpaRepository<RoadStaticInfoDetailHistory,RoadStaticInfoDetailHistoryKey> {
}
