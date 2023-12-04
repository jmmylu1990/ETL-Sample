package com.example.dao.mysql;

import com.example.model.entity.mysql.IntersectionEventDynamicHistory;
import com.example.model.entity.mysql.pk.IntersectionEventDynamicHistoryKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface IntersectionEventDynamicHistoryRepository extends JpaRepository<IntersectionEventDynamicHistory, IntersectionEventDynamicHistoryKey> {

    public List<IntersectionEventDynamicHistory> findAllByInfoDate(Date yesterday);
}
