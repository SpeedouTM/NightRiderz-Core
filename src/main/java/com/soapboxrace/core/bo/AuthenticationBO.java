package com.soapboxrace.core.bo;

import com.soapboxrace.core.dao.BanDAO;
import com.soapboxrace.core.jpa.BanEntity;
import com.soapboxrace.core.jpa.UserEntity;

import javax.ejb.EJB;
import javax.ejb.Stateless;

@Stateless
public class AuthenticationBO {
	@EJB
	private BanDAO banDAO;

	public BanEntity checkUserBan(UserEntity userEntity) {
		BanEntity userBan = banDAO.findByUser(userEntity);
		if (userBan != null) {
			return userBan;
		}
		String hwid = userEntity.getGameHardwareHash();
		if (hwid != null) {
			BanEntity hwidBan = banDAO.findByHardwareHash(hwid);
			if (hwidBan != null) {
				return hwidBan;
			}
		}
		String ip = userEntity.getIpAddress();
		if (ip != null) {
			return banDAO.findByIpAddress(ip);
		}
		return null;
	}
}
