package com.example.dao.mysql;

import com.example.model.entity.mysql.Dataset;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DatasetRepository extends JpaRepository<Dataset, Integer> {
	
	@Override
	@EntityGraph(value = "Dataset.fetchAll", type = EntityGraphType.FETCH)
	public List<Dataset> findAll();
	
	@EntityGraph(value = "Dataset.fetchAll", type = EntityGraphType.FETCH)
	public List<Dataset> findByEnable(boolean enable);
}
