package com.soapboxrace.core.dao;

import com.soapboxrace.core.dao.util.BaseDAO;
import com.soapboxrace.core.jpa.CarSlotEntity;
import com.soapboxrace.core.jpa.PersonaEntity;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;

@Stateless
public class CarSlotDAO extends BaseDAO<CarSlotEntity> {

	@PersistenceContext
	protected void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public CarSlotEntity findById(Long id) {
		return entityManager.find(CarSlotEntity.class, id);
	}

	public List<CarSlotEntity> findByPersonaId(Long personaId) {
		TypedQuery<CarSlotEntity> query = entityManager.createNamedQuery("CarSlotEntity.findByPersonaId", CarSlotEntity.class);
		query.setParameter("persona", personaId);
		return query.getResultList();
	}

	public List<CarSlotEntity> findByPersonaIdEager(Long personaId) {
		TypedQuery<CarSlotEntity> query = entityManager.createNamedQuery("CarSlotEntity.findByPersonaIdEager", CarSlotEntity.class);
		query.setParameter("persona", personaId);
		return query.getResultList();
	}

	public int countByPersonaId(Long personaId) {
		TypedQuery<Long> query = entityManager.createNamedQuery("CarSlotEntity.countByPersonaId", Long.class);
		query.setParameter("persona", personaId);
		return query.getSingleResult().intValue();
	}

	public CarSlotEntity getByPersonaIdEager(Long personaId, int index) {
		TypedQuery<CarSlotEntity> query = entityManager.createNamedQuery("CarSlotEntity.findByPersonaIdEager", CarSlotEntity.class);
		query.setParameter("persona", personaId);
		query.setFirstResult(index);
		query.setMaxResults(1);
		List<CarSlotEntity> list = query.getResultList();
		return list.isEmpty() ? null : list.get(0);
	}

	public void deleteByPersona(PersonaEntity personaEntity) {
		Query query = entityManager.createNamedQuery("CarSlotEntity.deleteByPersona");
		query.setParameter("persona", personaEntity);
		query.executeUpdate();
	}

}
