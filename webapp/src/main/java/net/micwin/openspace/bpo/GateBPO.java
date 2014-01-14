package net.micwin.openspace.bpo;

import java.util.Collection;

import net.micwin.openspace.entities.galaxy.Environment;
import net.micwin.openspace.entities.gates.Gate;
import net.micwin.openspace.entities.nanites.NaniteGroup;

/**
 * The bpo to handle gates.
 * 
 * @author MicWin
 * 
 */
public class GateBPO extends BaseBPO {

	/**
	 * The maximum group level that can travel through sub space gates.
	 */
	public static final int MAX_GROUP_LEVEL_FOR_GATE_TRAVEL = 3;

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
