package com.soapboxrace.core.bo;

import com.soapboxrace.core.bo.util.OwnedCarConverter;
import com.soapboxrace.core.dao.*;
import com.soapboxrace.core.jpa.*;
import com.soapboxrace.jaxb.http.CommerceResultStatus;
import com.soapboxrace.jaxb.http.OwnedCarTrans;
import com.soapboxrace.jaxb.util.UnmarshalXML;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.List;

@Stateless
public class BasketBO
{

    @EJB
    private PersonaBO personaBo;

    @EJB
    private ParameterBO parameterBO;

    @EJB
    private BasketDefinitionDAO basketDefinitionsDAO;

    @EJB
    private CarSlotDAO carSlotDAO;

    @EJB
    private OwnedCarDAO ownedCarDAO;

    @EJB
    private CustomCarDAO customCarDAO;

    @EJB
    private TokenSessionDAO tokenDAO;

    @EJB
    private ProductDAO productDao;

    @EJB
    private PersonaDAO personaDao;

    @EJB
    private TokenSessionBO tokenSessionBO;

    @EJB
    private InventoryDAO inventoryDao;

    @EJB
    private InventoryItemDAO inventoryItemDao;

    @EJB
    private AchievementDAO achievementDAO;

    @EJB
    private PersonaDAO personaDAO;

    @EJB
    private AchievementsBO achievementsBO;

    @EJB
    private CarBO carBO;

    @EJB
    private TreasureHuntDAO treasureHuntDAO;

    @EJB
    private CommerceBO commerceBO;

    private OwnedCarTrans getCar(String productId)
    {
        BasketDefinitionEntity basketDefinitonEntity = basketDefinitionsDAO.findById(productId);
        if (basketDefinitonEntity == null)
        {
            throw new IllegalArgumentException(String.format("No basket definition for %s", productId));
        }
        String ownedCarTrans = basketDefinitonEntity.getOwnedCarTrans();
        return UnmarshalXML.unMarshal(ownedCarTrans, OwnedCarTrans.class);
    }

    public CommerceResultStatus repairCar(String productId, PersonaEntity personaEntity)
    {
        CarSlotEntity defaultCarEntity = personaBo.getDefaultCarEntity(personaEntity.getPersonaId());
        float basePrice = productDao.findByProductId(productId).getUserPrice(personaEntity.getUser());
        int price = (int) (basePrice * (100 - defaultCarEntity.getOwnedCar().getDurability()));
        if (personaEntity.getCash() < price)
        {
            return CommerceResultStatus.FAIL_INSUFFICIENT_FUNDS;
        }
        if (parameterBO.getBoolParam("ENABLE_ECONOMY"))
        {
            personaEntity.setCash(personaEntity.getCash() - price);
        }
        personaDao.update(personaEntity);

        defaultCarEntity.getOwnedCar().setDurability(100);

        carSlotDAO.update(defaultCarEntity);
        return CommerceResultStatus.SUCCESS;
    }

    public CommerceResultStatus reviveTh(String productId, PersonaEntity personaEntity)
    {
        ProductEntity product = productDao.findByProductId(productId);
        if (personaEntity.getCash() < product.getUserPrice(personaEntity.getUser()))
        {
            return CommerceResultStatus.FAIL_LOCKED_PRODUCT_NOT_ACCESSIBLE_TO_THIS_USER;
        }
        if (parameterBO.getBoolParam("ENABLE_ECONOMY"))
        {
            personaEntity.setCash(personaEntity.getCash() - product.getUserPrice(personaEntity.getUser()));
            personaDao.update(personaEntity);
        }

        TreasureHuntEntity th = treasureHuntDAO.findById(personaEntity.getPersonaId());
        th.setIsStreakBroken(false);
        treasureHuntDAO.update(th);

        return CommerceResultStatus.SUCCESS;
    }

    public CommerceResultStatus buyPowerups(String productId, PersonaEntity personaEntity)
    {
        if (!parameterBO.getBoolParam("ENABLE_ECONOMY"))
        {
            return CommerceResultStatus.FAIL_INSUFFICIENT_FUNDS;
        }
        ProductEntity powerupProduct = productDao.findByProductId(productId);
        InventoryEntity inventoryEntity = inventoryDao.findByPersonaId(personaEntity.getPersonaId());

        if (powerupProduct == null)
        {
            return CommerceResultStatus.FAIL_INVALID_BASKET;
        }

        if (personaEntity.getCash() < powerupProduct.getUserPrice(personaEntity.getUser()))
        {
            return CommerceResultStatus.FAIL_INSUFFICIENT_FUNDS;
        }

        InventoryItemEntity item = null;

        for (InventoryItemEntity i : inventoryEntity.getItems())
        {
            if (i.getHash().equals(powerupProduct.getHash().intValue()))
            {
                item = i;
                break;
            }
        }

        if (item == null)
        {
            return CommerceResultStatus.FAIL_INVALID_BASKET;
        }

        int maxUsage;
        if (personaEntity.getUser().isPremium()) {
            maxUsage = parameterBO.getIntParam("MAX_POWERUPS_PREMIUM", 250);
        } else {
            maxUsage = parameterBO.getIntParam("MAX_POWERUPS_FREE", 250);
        }

        int newUsageCount = item.getRemainingUseCount() + 15;

        if (newUsageCount > maxUsage)
            newUsageCount = maxUsage;

        if (newUsageCount > item.getRemainingUseCount())
        {
            personaEntity.setCash(personaEntity.getCash() - powerupProduct.getUserPrice(personaEntity.getUser()));
            personaDao.update(personaEntity);
        }

        item.setRemainingUseCount(newUsageCount);
        inventoryItemDao.update(item);

        return CommerceResultStatus.SUCCESS;
    }

    public CommerceResultStatus buyCar(String productId, PersonaEntity personaEntity, String securityToken)
    {
        int carCount = carSlotDAO.countByPersonaId(personaEntity.getPersonaId());
        if (carCount >= parameterBO.getCarLimit(securityToken))
        {
            return CommerceResultStatus.FAIL_INSUFFICIENT_CAR_SLOTS;
        }

        ProductEntity productEntity = productDao.findByProductId(productId);
        if (productEntity == null || personaEntity.getCash() < productEntity.getUserPrice(personaEntity.getUser()))
        {
            return CommerceResultStatus.FAIL_INSUFFICIENT_FUNDS;
        }

        CarSlotEntity carSlotEntity = addCar(productId, personaEntity);

        if (parameterBO.getBoolParam("ENABLE_ECONOMY"))
        {
            personaEntity.setCash(personaEntity.getCash() - productEntity.getUserPrice(personaEntity.getUser()));
        }
        personaDao.update(personaEntity);

        personaBo.changeDefaultCar(personaEntity.getPersonaId(), carSlotEntity.getOwnedCar().getId());
        return CommerceResultStatus.SUCCESS;
    }

    public CarSlotEntity addCar(String productId, PersonaEntity personaEntity)
    {
        ProductEntity productEntity = productDao.findByProductId(productId);
        OwnedCarTrans ownedCarTrans = getCar(productId);
        ownedCarTrans.setId(0L);
        ownedCarTrans.getCustomCar().setId(0);
        CarSlotEntity carSlotEntity = new CarSlotEntity();
        carSlotEntity.setPersona(personaEntity);

        OwnedCarEntity ownedCarEntity = new OwnedCarEntity();
        ownedCarEntity.setCarSlot(carSlotEntity);
        CustomCarEntity customCarEntity = new CustomCarEntity();
        customCarEntity.setOwnedCar(ownedCarEntity);
        ownedCarEntity.setCustomCar(customCarEntity);
        carSlotEntity.setOwnedCar(ownedCarEntity);
        OwnedCarConverter.trans2Entity(ownedCarTrans, ownedCarEntity);
        OwnedCarConverter.details2NewEntity(ownedCarTrans, ownedCarEntity);
        commerceBO.calcNewCarClass(customCarEntity);

        carSlotDAO.insert(carSlotEntity);

        String longDesc = productEntity.getLongDescription();
        String carId = longDesc.split("_")[1];
        String brand = carBO.getBrand(carId);

        AchievementDefinitionEntity achievement = achievementDAO.findByName("achievement_ACH_OWN_" + brand);

        if (achievement != null)
        {
            achievementsBO.update(personaEntity, achievement, 1L);
        }

        AchievementDefinitionEntity achievement2 = achievementDAO.findByName("achievement_ACH_OWN_CAR");

        if (achievement2 != null)
        {
            achievementsBO.update(personaEntity, achievement2, 1L);
        }

        return carSlotEntity;
    }

    public List<CarSlotEntity> getPersonasCar(Long personaId)
    {
        List<CarSlotEntity> findByPersonaId = carSlotDAO.findByPersonaIdEager(personaId);
        for (CarSlotEntity carSlotEntity : findByPersonaId)
        {
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
        }
        return findByPersonaId;
    }

    public boolean sellCar(String securityToken, Long personaId, Long serialNumber)
    {
        this.tokenSessionBO.verifyPersona(securityToken, personaId);

        OwnedCarEntity ownedCarEntity = ownedCarDAO.findById(serialNumber);
        if (ownedCarEntity == null)
        {
            return false;
        }
        CarSlotEntity carSlotEntity = ownedCarEntity.getCarSlot();
        if (carSlotEntity == null)
        {
            return false;
        }
        int personaCarCount = carSlotDAO.countByPersonaId(personaId);
        if (personaCarCount <= 1)
        {
            return false;
        }

        PersonaEntity personaEntity = personaDao.findById(personaId);

        final int maxCash = parameterBO.getMaxCash(securityToken);
        if (personaEntity.getCash() < maxCash)
        {
            int cashTotal = (int) (personaEntity.getCash() + ownedCarEntity.getCustomCar().getResalePrice());
            if (parameterBO.getBoolParam("ENABLE_ECONOMY"))
            {
                personaEntity.setCash(Math.max(0, Math.min(maxCash, cashTotal)));
            }
        }

        CarSlotEntity defaultCarEntity = personaBo.getDefaultCarEntity(personaId);

        int curCarIndex = personaEntity.getCurCarIndex();
        if (defaultCarEntity.getId().equals(carSlotEntity.getId()))
        {
            curCarIndex = 0;
        } else
        {
            List<CarSlotEntity> personasCar = carSlotDAO.findByPersonaId(personaId);
            int curCarIndexTmp = curCarIndex;
            for (int i = 0; i < curCarIndexTmp; i++)
            {
                if (personasCar.get(i).getId().equals(carSlotEntity.getId()))
                {
                    curCarIndex--;
                    break;
                }
            }
        }
        carSlotDAO.delete(carSlotEntity);
        personaEntity.setCurCarIndex(curCarIndex);
        personaDao.update(personaEntity);
        return true;
    }

}
