package com.example.dao.mysql;

import com.example.model.entity.mysql.IntersectionTurnDynamicTurnHistory;
import com.example.model.entity.mysql.pk.IntersectionTurnDynamicTurnHistoryKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.Embeddable;

@Repository
public interface IntersectionTurnDynamicTurnHistoryRepository extends JpaRepository<IntersectionTurnDynamicTurnHistory, IntersectionTurnDynamicTurnHistoryKey> {
}
