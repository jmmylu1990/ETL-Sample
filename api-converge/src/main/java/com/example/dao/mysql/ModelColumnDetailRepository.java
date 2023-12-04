package com.example.dao.mysql;

import com.example.model.entity.mysql.ModelColumnDetail;
import com.example.model.entity.mysql.pk.ModelColumnDetailPK;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ModelColumnDetailRepository extends JpaRepository<ModelColumnDetail, ModelColumnDetailPK> {

	@EntityGraph(value = "ModelColumnDetail.fetchAll", type = EntityGraph.EntityGraphType.LOAD)
	public List<ModelColumnDetail> findBydBelongModel(String modelName);
}
