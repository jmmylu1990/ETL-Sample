package com.example.dao.mysql;

import com.example.model.entity.mysql.TntersectionTurnDynamicDirection;
import com.example.model.entity.mysql.pk.TntersectionTurnDynamicDirectionKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IntersectionTurnDynamicDirectionRepository extends JpaRepository<TntersectionTurnDynamicDirection, TntersectionTurnDynamicDirectionKey> {
}
