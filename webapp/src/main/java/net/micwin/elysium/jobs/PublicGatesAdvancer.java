package net.micwin.elysium.jobs;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.micwin.elysium.dao.DaoManager;
import net.micwin.elysium.entities.SysParam;
import net.micwin.elysium.entities.characters.Avatar;
import net.micwin.elysium.entities.characters.User.Role;
import net.micwin.elysium.entities.gates.Gate;

public class PublicGatesAdvancer {

	private static final Log L = LogFactory.getLog(PublicGatesAdvancer.class);

	public void advance() {
		// collect all active home gate adreses‚
		HashSet<String> homeGateAdresses = new HashSet<String>();
		for (Avatar avatar : DaoManager.I.getAvatarDao().loadAll(new LinkedList<Avatar>())) {
			if (avatar.getUser().getRole() != Role.ADMIN)
				homeGateAdresses.add(avatar.getHomeGateAdress());
		}

		// get all gates not in that list

		Collection<Gate> unhomeGates = DaoManager.I.getGatesDao().findByNotHavingAdress(homeGateAdresses);

		StringBuffer paramValueBuffer = new StringBuffer();
		for (Gate gate : unhomeGates) {
			if (gate.getGatePass() != null) {
				if (L.isDebugEnabled()) {
					L.debug("omitting gate " + gate.getGateAdress() + " - is locked");
				}
				continue;
			}
			if (paramValueBuffer.length() > 0) {
				paramValueBuffer.append(',');
			}
			paramValueBuffer.append(gate.getGateAdress());
		}

		String paramValue = paramValueBuffer.toString();

		SysParam publicGatesParam = DaoManager.I.getSysParamDao().findByKey("publicGates", null);
		if (publicGatesParam == null) {
			publicGatesParam = DaoManager.I.getSysParamDao().create("publicGates", paramValue);
		} else {
			publicGatesParam.setValue(paramValue);
			DaoManager.I.getSysParamDao().update(publicGatesParam, true);
		}

		if (L.isDebugEnabled()) {
			L.debug(" new list of public gates is " + paramValue);
		}

	}

}
