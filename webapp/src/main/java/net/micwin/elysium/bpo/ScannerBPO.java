package net.micwin.elysium.bpo;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import net.micwin.elysium.entities.NaniteGroup;
import net.micwin.elysium.entities.NaniteGroup.State;
import net.micwin.elysium.entities.appliances.Appliance;
import net.micwin.elysium.entities.appliances.Utilization;
import net.micwin.elysium.entities.galaxy.Planet;
import net.micwin.elysium.entities.galaxy.SolarSystem;
import net.micwin.elysium.entities.gates.Gate;

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
	 * The energy signature strength one nanite adds to the group total
	 * signature.
	 */
	private static final double BASE_NANITE_SIGNATURE_SIZE = 0.1;

	/**
	 * The smallest energy signature a unit can detect when having sensors at
	 * level 0.
	 */
	private static final double BASE_SENSOR_STRENGTH = 1;

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

	/**
	 * Scans for other nanite groups on same location. Does raise scanner
	 * talent.
	 * 
	 * @param scanningGroup
	 * @return
	 */

	public List<NaniteGroup> scanForOtherNaniteGroups(NaniteGroup scanningGroup) {

		List<NaniteGroup> found = getNanitesDao().findByEnvironment(scanningGroup.getPosition().getEnvironment());

		List<NaniteGroup> result = new LinkedList<NaniteGroup>();

		int hiddenCount = 0;

		for (NaniteGroup targetGroup : found) {

			// same id
			if (targetGroup.getId().equals(scanningGroup.getId())) {
				continue;
			}

			// same controller
			if (scanningGroup.getController().equals(targetGroup.getController())) {
				result.add(targetGroup);
				continue;
			}

			// visible by sensors?

			if (isVisibleBy(targetGroup, scanningGroup)) {
				result.add(targetGroup);
			} else {
				hiddenCount++;
				if (targetGroup.getState() == State.ENTRENCHED) {
					raiseUsage(targetGroup.getController(), Appliance.EMISSION_CONTROL, true);
				}
			}

		}
		if (result.size() > 0) {
			raiseUsage(scanningGroup.getController(), Appliance.SHORT_RANGE_SCANS, false);
		}

		if (hiddenCount > 0) {
			raiseUsage(scanningGroup.getController(), Appliance.EMISSION_CONTROL, true);

		}
		return result;
	}

	/**
	 * Checks wether or not target is scannable by scanner,
	 * 
	 * @param target
	 * @param scanner
	 * @return
	 */
	public boolean isVisibleBy(NaniteGroup target, NaniteGroup scanner) {
		double signatureStrength = computeSignatureStrength(target);
		double lowestValue = computeLowestVisibleSignature(scanner);

		return signatureStrength >= lowestValue;
	}

	/**
	 * Computes the lowest visible signature strength this scanner can see.
	 * 
	 * @param scanner
	 * @return
	 */
	public double computeLowestVisibleSignature(NaniteGroup scanner) {
		return BASE_SENSOR_STRENGTH * Math.pow(0.95, computeShortRangeSensorStrength(scanner));
	}

	/**
	 * Computes strength of energy emission signature. The bigger, the better
	 * this group can get scanned by others, the smaller this group can sneak
	 * better.
	 * 
	 * @param naniteGroup
	 * @return
	 */
	public double computeSignatureStrength(NaniteGroup naniteGroup) {

		if (L.isDebugEnabled()) {
			L.debug("computing signature strength for target " + naniteGroup);
		}
		double signatureStrength = BASE_NANITE_SIGNATURE_SIZE * naniteGroup.getNaniteCount()
						* naniteGroup.getState().getSignatureFactor();

		Utilization ec = getTalent(naniteGroup.getController(), Appliance.EMISSION_CONTROL);

		if (ec != null && ec.getLevel() > 0) {
			signatureStrength *= Math.pow(0.95, ec.getLevel());
		}

		return signatureStrength;
	}

	/**
	 * Computes the short range sensor strength of this group, ie a measure for
	 * the ability of this group to find other (or cloaked) units. The higher,
	 * the smaller emmissions can get detected and hence the more and the
	 * smaller and better cloaked units can be seen.
	 * 
	 * @param group
	 * @return
	 */
	public long computeShortRangeSensorStrength(NaniteGroup group) {
		return (long) (getTalent(group.getController(), Appliance.SHORT_RANGE_SCANS).getLevel() * group.getState()
						.getSensorFactor());
	}

	/**
	 * Determnine wether or not nanite group scanner can scan details like the
	 * exact count or avatar level of other group.
	 * 
	 * @param scannerGroup
	 * @param otherGroup
	 * @return
	 */
	public boolean canScanDetails(NaniteGroup scannerGroup, NaniteGroup otherGroup) {

		if (scannerGroup.getController().equals(otherGroup.getController())) {
			return true;
		}

		Utilization scanner = getTalent(scannerGroup.getController(), Appliance.SHORT_RANGE_SCANS);
		int scannerStrength = scanner.getLevel() + scannerGroup.getController().getLevel();

		Utilization ecp = getTalent(otherGroup.getController(), Appliance.EMISSION_CONTROL);
		int ecpStrength = ecp != null ? ecp.getLevel() : 0;
		ecpStrength += otherGroup.getController().getLevel();

		return scannerStrength > ecpStrength;
	}

}
