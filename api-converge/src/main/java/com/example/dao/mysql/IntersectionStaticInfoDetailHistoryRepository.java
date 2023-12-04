package com.example.dao.mysql;

import com.example.model.entity.mysql.IntersectionStaticInfoDetailHistory;
import com.example.model.entity.mysql.IntersectionStaticInfoDetailHistoryKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IntersectionStaticInfoDetailHistoryRepository extends JpaRepository<IntersectionStaticInfoDetailHistory, IntersectionStaticInfoDetailHistoryKey> {
}
