package com.example.dao.mysql;

import com.example.model.entity.mysql.IntersectionTurnDynamicTurn;
import com.example.model.entity.mysql.pk.IntersectionTurnDynamicTurnKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IntersectionTurnDynamicTurnRespository extends JpaRepository<IntersectionTurnDynamicTurn, IntersectionTurnDynamicTurnKey> {
}
