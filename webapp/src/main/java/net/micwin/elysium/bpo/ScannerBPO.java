package net.micwin.elysium.bpo;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import net.micwin.elysium.entities.NaniteGroup;
import net.micwin.elysium.entities.galaxy.Planet;
import net.micwin.elysium.entities.galaxy.SolarSystem;
import net.micwin.elysium.entities.gates.Gate;

import org.apache.wicket.MarkupContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A bpo that handles scanning.
 * 
 * @author MicWin
 * 
 */
public class ScannerBPO extends BaseBPO {

	private static final Logger L = LoggerFactory.getLogger(ScannerBPO.class);

	/**
	 * Scans for gates in the same solar system as group. Only works if the
	 * group is on a planet that is part of a solar system.
	 * 
	 * @param group
	 * @return
	 */
	public List<Gate> scanForGates(NaniteGroup group) {

		// navigate to solar system. This only works if the nanite group is on a
		// planet of a solar system that has at least one planet
		SolarSystem system = (SolarSystem) (group.getPosition().getEnvironment().getPosition().getEnvironment());

		List<Gate> result = new LinkedList<Gate>();

		// collect planets and lookup gates. We do it that way to avoid depth
		// issues of hibernate

		for (Planet planet : system.getPlanets()) {

			Collection<Gate> gates = getGatesDao().findByEnvironment(planet);
			result.addAll(gates);
		}

		return result;

	}

	public List<NaniteGroup> scanForOtherNaniteGroups(NaniteGroup scanningGroup) {

		List<NaniteGroup> found = getNanitesDao().findByEnvironment(scanningGroup.getPosition().getEnvironment());

		List<NaniteGroup> result = new LinkedList<NaniteGroup>();

		for (NaniteGroup naniteGroup : found) {
			if (!naniteGroup.getId().equals(scanningGroup.getId())) {
				result.add(naniteGroup);
			}
		}

		return result;
	}
}
