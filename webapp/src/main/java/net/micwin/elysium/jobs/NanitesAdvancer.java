package net.micwin.elysium.jobs;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.micwin.elysium.bpo.MessageBPO;
import net.micwin.elysium.bpo.NaniteBPO;
import net.micwin.elysium.dao.DaoManager;
import net.micwin.elysium.entities.nanites.NaniteGroup;
import net.micwin.elysium.entities.nanites.NaniteGroup.State;

public class NanitesAdvancer {

	private static final Logger L = LoggerFactory.getLogger(NanitesAdvancer.class);

	public void advance() {

		L.info("running nanites advancer ...");

		int changeCount = advanceEntrenching();
		changeCount += advanceUpgrading();

		L.info(changeCount + " groups advanced");
	}

	private int advanceUpgrading() {

		Collection<NaniteGroup> result = DaoManager.I.getNanitesDao().findByState(State.UPGRADING);

		L.info(result.size() + " groups upgrading ...");

		int changeCount = 0;

		NaniteBPO naniteBpo = new NaniteBPO();
		MessageBPO messageBpo = new MessageBPO();

		for (NaniteGroup naniteGroup : result) {

			long maxStructurePoints = naniteBpo.computeStructurePoints(naniteGroup);

			long newStructurePoints = Math.min(maxStructurePoints,
							naniteGroup.getStructurePoints() + naniteGroup.getNaniteCount() / 10);
			naniteGroup.setStructurePoints(newStructurePoints);
			if (newStructurePoints == maxStructurePoints) {
				naniteGroup.setState(State.IDLE);
				String msg = "Aufrüsten abgeschlossen. Wir haben jetzt Stufe " + naniteGroup.getGroupLevel() + ".";

				if (naniteGroup.getNaniteCount() < naniteGroup.getMinNaniteCount()) {
					msg += " Unsere minimale Stärke beträgt "
									+ naniteGroup.getMinNaniteCount()
									+ ". Wir sollten noch die Nanitenzahl erhöhen, da wir sonst nich tmit voller Schlagkraft zuschlagen können.";
				}
				messageBpo.send(naniteGroup, naniteGroup.getController(), msg);
			} else {
				L.info("set new structure points :" + naniteGroup.getStructurePoints() + " to go : "
								+ (maxStructurePoints - naniteGroup.getStructurePoints()));
			}
			DaoManager.I.getNanitesDao().update(naniteGroup);
			changeCount++;

		}

		L.info(changeCount + " groups upgraded.");
		return changeCount;
	}

	public int advanceEntrenching() {
		Collection<NaniteGroup> result = DaoManager.I.getNanitesDao().findByState(State.ENTRENCHING);
		int changeCount = 0;

		for (Iterator<NaniteGroup> iterator = result.iterator(); iterator.hasNext();) {
			NaniteGroup naniteGroup = iterator.next();

			boolean changedSomething = advanceEntrenching(naniteGroup);

			if (changedSomething) {
				changeCount++;
				DaoManager.I.getNanitesDao().update(naniteGroup);
			}

		}
		return changeCount;
	}

	/**
	 * Checks wether this group is entrenching and if, check dates.
	 * 
	 * @param naniteGroup
	 * @return
	 */
	public boolean advanceEntrenching(NaniteGroup naniteGroup) {

		if (naniteGroup.getState() != State.ENTRENCHING) {
			return false;
		}

		if (naniteGroup.getStateEndGT().before(new Date())) {
			naniteGroup.setStateEndGT(null);
			naniteGroup.setState(State.ENTRENCHED);
			new MessageBPO().send(naniteGroup, naniteGroup.getController(), "Eingraben beendet.");
			return true;
		}
		L.debug("nanite group still has to wait until " + naniteGroup.getStateEndGT() + " ");
		return false;
	}

}
