package net.micwin.elysium.jobs;

import java.util.Collection;

import net.micwin.elysium.dao.DaoManager;
import net.micwin.elysium.entities.SysParam;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PublicGatesAdvancer {

	private static final Log L = LogFactory.getLog(PublicGatesAdvancer.class);

	public void advance() {

		L.info("reanalyzing public gates...");

		// get all gates not in that list
		Collection<String> publicGateAdresses = DaoManager.I.getGatesDao().findPublicGateAdresses();

		StringBuffer paramValueBuffer = new StringBuffer();
		for (String gateAdress : publicGateAdresses) {
			if (paramValueBuffer.length() > 0) {
				paramValueBuffer.append(',');
			}
			paramValueBuffer.append(gateAdress);
		}

		String paramValue = paramValueBuffer.toString();

		SysParam publicGatesParam = DaoManager.I.getSysParamDao().findByKey("publicGates", null);
		if (publicGatesParam == null) {
			publicGatesParam = DaoManager.I.getSysParamDao().create("publicGates", paramValue);
		} else {
			publicGatesParam.setValue(paramValue);
			DaoManager.I.getSysParamDao().update(publicGatesParam);
		}

		if (L.isDebugEnabled()) {
			L.debug(" new list of public gates is " + paramValue);
		}

	}

}
