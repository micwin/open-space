package net.micwin.elysium.bpo;

import java.util.Collection;

import net.micwin.elysium.model.galaxy.Environment;
import net.micwin.elysium.model.gates.Gate;

/**
 * The bpo to handle gates.
 * 
 * @author MicWin
 * 
 */
public class GateBPO extends BaseBPO {

	/**
	 * Get the gates an specified planet.
	 * 
	 * @param planet
	 * @return
	 */
	public Collection<Gate> getGatesAt(Environment planet) {
		return getGatesDao().findByEnvironment(planet);

	}

}
