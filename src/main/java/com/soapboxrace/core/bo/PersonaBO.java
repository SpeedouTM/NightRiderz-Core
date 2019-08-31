package com.soapboxrace.core.bo;

import com.soapboxrace.core.bo.util.OwnedCarConverter;
import com.soapboxrace.core.dao.*;
import com.soapboxrace.core.jpa.*;
import com.soapboxrace.jaxb.http.BadgeBundle;
import com.soapboxrace.jaxb.http.BadgeInput;
import com.soapboxrace.jaxb.http.BadgePacket;
import com.soapboxrace.jaxb.http.OwnedCarTrans;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class PersonaBO {

	@EJB
	private PersonaDAO personaDAO;

	@EJB
	private CarSlotDAO carSlotDAO;

	@EJB
	private LevelRepDAO levelRepDAO;

	@EJB
	private OwnedCarDAO ownedCarDAO;
	
	@EJB
	private BadgeDefinitionDAO badgeDefinitionDAO;
	
	@EJB
	private AchievementDAO achievementDAO;
	
	@EJB
	private PersonaAchievementRankDAO personaAchievementRankDAO;

	@EJB
	private CommerceBO commerceBO;

	@EJB
	private CustomCarDAO customCarDAO;

	public void updateBadges(long idPersona, BadgeBundle badgeBundle) {
		PersonaEntity persona = personaDAO.findById(idPersona);

		List<BadgePacket> badgePackets = new ArrayList<>();

		for (BadgeInput input : badgeBundle.getBadgeInputs()) {
			BadgeDefinitionEntity badge = badgeDefinitionDAO.findById((long) input.getBadgeDefinitionId());
			if (badge == null) continue;

			AchievementDefinitionEntity achievement = achievementDAO.findByBadgeId(badge.getId());

			if (achievement == null) continue;

			BadgePacket packet = new BadgePacket();
			packet.setSlotId(input.getSlotId());
			packet.setBadgeDefinitionId(badge.getId().intValue());

			List<AchievementRankEntity> ranks = personaAchievementRankDAO
					.findAllForPersonaAchievement(persona, achievement)
					.stream()
					.filter(pr -> !pr.getState().equals("Locked") && !pr.getState().equals("InProgress"))
					.map(PersonaAchievementRankEntity::getRank)
					.collect(Collectors.toList());

			if (ranks.isEmpty()) {
				packet.setIsRare(false);
				packet.setRarity(0f);
				packet.setAchievementRankId(-1);
			} else {
				packet.setIsRare(ranks.get(ranks.size() - 1).isRare());
				packet.setRarity(0.0f);
				packet.setAchievementRankId(Math.toIntExact(ranks.get(ranks.size() - 1).getId()));
			}

			badgePackets.add(packet);
		}

		persona.setBadges(badgePackets);
		personaDAO.update(persona);
	}
	
	public void changeDefaultCar(Long personaId, Long defaultCarId) {
		PersonaEntity personaEntity = personaDAO.findById(personaId);
		List<CarSlotEntity> carSlotList = carSlotDAO.findByPersonaId(personaId);
		int i = 0;
		for (CarSlotEntity carSlotEntity : carSlotList) {
			if (carSlotEntity.getOwnedCar().getId().equals(defaultCarId)) {
				break;
			}
			i++;
		}
		personaEntity.setCurCarIndex(i);
		personaDAO.update(personaEntity);
	}

	public PersonaEntity getPersonaById(Long personaId) {
		return personaDAO.findById(personaId);
	}

	public CarSlotEntity getDefaultCarEntity(Long personaId) {
		int carSlotCount = carSlotDAO.countByPersonaId(personaId);
		if (carSlotCount > 0) {
			PersonaEntity personaEntity = personaDAO.findById(personaId);
			int curCarIndex = personaEntity.getCurCarIndex();
			if (curCarIndex >= carSlotCount) {
				curCarIndex = carSlotCount - 1;
				personaEntity.setCurCarIndex(curCarIndex);
				personaDAO.update(personaEntity);
			}
			CarSlotEntity carSlotEntity = carSlotDAO.getByPersonaIdEager(personaId, curCarIndex);
			CustomCarEntity customCar = carSlotEntity.getOwnedCar().getCustomCar();
			if (customCar.getCarClassHash() == 0) {
				commerceBO.calcNewCarClass(customCar);
				customCarDAO.update(customCar);
			}
			customCar.getPaints().size();
			customCar.getPerformanceParts().size();
			customCar.getSkillModParts().size();
			customCar.getVisualParts().size();
			customCar.getVinyls().size();
			return carSlotEntity;
		}
		return null;
	}

	public OwnedCarTrans getDefaultCar(Long personaId) {
		CarSlotEntity carSlotEntity = getDefaultCarEntity(personaId);
		if (carSlotEntity == null) {
			return new OwnedCarTrans();
		}
		return OwnedCarConverter.entity2Trans(carSlotEntity.getOwnedCar());
	}

	public LevelRepEntity getLevelInfoByLevel(Long level) {
		return levelRepDAO.findByLevel(level);
	}

	public OwnedCarEntity getCarByOwnedCarId(Long ownedCarId) {
		OwnedCarEntity ownedCarEntity = ownedCarDAO.findByIdEager(ownedCarId);
		CustomCarEntity customCar = ownedCarEntity.getCustomCar();
		if (customCar.getCarClassHash() == 0) {
			commerceBO.calcNewCarClass(customCar);
			customCarDAO.update(customCar);
		}
		customCar.getPaints().size();
		customCar.getPerformanceParts().size();
		customCar.getSkillModParts().size();
		customCar.getVisualParts().size();
		customCar.getVinyls().size();
		return ownedCarEntity;
	}

}
