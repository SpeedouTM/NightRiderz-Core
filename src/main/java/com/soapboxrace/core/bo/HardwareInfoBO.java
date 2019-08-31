package com.soapboxrace.core.bo;

import com.soapboxrace.core.dao.BanDAO;
import com.soapboxrace.core.jpa.BanEntity;
import com.soapboxrace.jaxb.http.HardwareInfo;
import com.soapboxrace.jaxb.util.MarshalXML;
import org.apache.commons.codec.digest.DigestUtils;

import javax.ejb.EJB;
import javax.ejb.Stateless;

@Stateless
public class HardwareInfoBO {
	@EJB
	private BanDAO banDAO;

	public String calcHardwareInfoHash(HardwareInfo hardwareInfo) {
		// Better would be to clone hardwareInfo first
		hardwareInfo.setAvailableMem(0);
		hardwareInfo.setCpuid10(0);
		hardwareInfo.setCpuid11(0);
		hardwareInfo.setCpuid12(0);
		hardwareInfo.setCpuid13(0);
		hardwareInfo.setUserID(0);

		// https://docs.microsoft.com/en-us/windows/desktop/sysinfo/operating-system-version
		// "Applications not manifested for Windows 8.1 or Windows 10 will return the Windows 8 OS version value (6.2)."
		// HOWEVER, it seems that this restriction will not apply when running inside of Sandboxie
		if (hardwareInfo.getOsMajorVersion() > 6 ||
				(hardwareInfo.getOsMajorVersion() == 6 && hardwareInfo.getOsMinorVersion() > 2)) {
			hardwareInfo.setOsMajorVersion(6);
			hardwareInfo.setOsMinorVersion(2);
			hardwareInfo.setOsBuildNumber(9200);
		}

		String hardwareInfoXml = MarshalXML.marshal(hardwareInfo);
		return calcHardwareInfoHash(hardwareInfoXml);
	}

	public String calcHardwareInfoHash(String hardwareInfoXml) {
		if (hardwareInfoXml != null && !hardwareInfoXml.isEmpty()) {
			return DigestUtils.sha1Hex(hardwareInfoXml);
		}
		return "empty";
	}

	public boolean isHardwareHashBanned(String hardwareHash) {
		BanEntity banEntity = banDAO.findByHardwareHash(hardwareHash);
		return banEntity != null;
	}

}
