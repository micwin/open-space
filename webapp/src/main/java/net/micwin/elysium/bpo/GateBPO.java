package net.micwin.elysium.bpo;

import java.util.Collection;

import net.micwin.elysium.entities.NaniteGroup;
import net.micwin.elysium.entities.galaxy.Environment;
import net.micwin.elysium.entities.gates.Gate;

/**
 * The bpo to handle gates.
 * 
 * @author MicWin
 * 
 */
public class GateBPO extends BaseBPO {

	public Gate getGateAt(Environment environment) {
		Collection<Gate> gates = getGatesDao().findByEnvironment(environment);
		if (gates.size() > 0)
			return gates.iterator().next();
		else
			return null;
	}

	public Gate getGateAt(NaniteGroup naniteGroup) {

		return getGateAt(naniteGroup.getPosition().getEnvironment());

	}

	/**
	 * Checks wethrr or not specified environment has a public gate, ie a gate
	 * without password (incoming and outgoing).
	 * 
	 * @param environment
	 * @return
	 */
	public boolean hasPublicGate(Environment environment) {
		Gate gate = getGateAt(environment);
		if (gate == null) {
			return false;
		}

		return gate.getGatePass() == null;
	}
}
