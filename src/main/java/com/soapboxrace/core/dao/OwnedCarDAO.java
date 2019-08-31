package com.soapboxrace.core.dao;

import com.soapboxrace.core.dao.util.BaseDAO;
import com.soapboxrace.core.jpa.OwnedCarEntity;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Stateless
public class OwnedCarDAO extends BaseDAO<OwnedCarEntity> {

	@PersistenceContext
	protected void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public OwnedCarEntity findById(Long id) {
		return entityManager.find(OwnedCarEntity.class, id);
	}

	public OwnedCarEntity findByIdEager(Long id) {
		TypedQuery<OwnedCarEntity> query = entityManager.createQuery("SELECT obj FROM OwnedCarEntity obj " +
				"INNER JOIN FETCH obj.customCar cc " +
				"WHERE obj.id = :id",
				OwnedCarEntity.class
		);
		query.setParameter("id", id);
		List<OwnedCarEntity> list = query.getResultList();
		return list.isEmpty() ? null : list.get(0);
	}
}
