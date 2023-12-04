package com.example.dao.mysql;

import com.example.model.entity.mysql.IntersectionStaticInfoDetail;
import com.example.model.entity.mysql.IntersectionStaticInfoDetailHistory;
import com.example.model.entity.mysql.IntersectionStaticInfoDetailHistoryKey;
import com.example.model.entity.mysql.pk.IntersectionStaticInfoDetailKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IntersectionStaticInfoDetailRepository extends JpaRepository<IntersectionStaticInfoDetailHistory, IntersectionStaticInfoDetailHistoryKey> {
}
