package net.micwin.elysium.bpo;

import java.util.Collection;

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

}
