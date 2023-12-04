package com.example.dao.mysql;

import com.example.model.entity.mysql.RoadStaticInfoMasterHistory;
import com.example.model.entity.mysql.pk.RoadStaticInfoMasterHistoryKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoadStaticInfoMasterHistoryRepository extends JpaRepository<RoadStaticInfoMasterHistory, RoadStaticInfoMasterHistoryKey> {
}
