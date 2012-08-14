package net.micwin.elysium.jobs;

/*
 (c) 2012 micwin.net

 This file is part of open-space.

 open-space is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 open-space is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero Public License for more details.

 You should have received a copy of the GNU Affero Public License
 along with open-space.  If not, see http://www.gnu.org/licenses.

 Diese Datei ist Teil von open-space.

 open-space ist Freie Software: Sie können es unter den Bedingungen
 der GNU Affero Public License, wie von der Free Software Foundation,
 Version 3 der Lizenz oder (nach Ihrer Option) jeder späteren
 veröffentlichten Version, weiterverbreiten und/oder modifizieren.

 open-space wird in der Hoffnung, dass es nützlich sein wird, aber
 OHNE JEDE GEWÄHRLEISTUNG, bereitgestellt; sogar ohne die implizite
 Gewährleistung der MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK.
 Siehe die GNU Affero Public License für weitere Details.

 Sie sollten eine Kopie der GNU Affero Public License zusammen mit diesem
 Programm erhalten haben. Wenn nicht, siehe http://www.gnu.org/licenses. 

 */

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TimerTask;

import net.micwin.elysium.bpo.GateBPO;
import net.micwin.elysium.bpo.MessageBPO;
import net.micwin.elysium.bpo.NaniteBPO;
import net.micwin.elysium.dao.DaoManager;
import net.micwin.elysium.entities.GalaxyTimer;
import net.micwin.elysium.entities.NaniteGroup;
import net.micwin.elysium.entities.NaniteGroup.State;
import net.micwin.elysium.entities.gates.Gate;
import net.micwin.elysium.entities.messaging.Message;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class AdvancerTask extends TimerTask {

	private static final Logger L = LoggerFactory.getLogger(AdvancerTask.class);

	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public void run() {
		L.info("running @ galaxy time " + GalaxyTimer.get().getGalaxyDate() + "...");

		Session session = sessionFactory.openSession();

		try {

			runNanitesAdvancer();
			advanceArena();

		} catch (Exception e) {

			L.error("exception while advancer loop", e);
		}

		session.flush();
		session.close();

		L.info("done");
	}

	private void advanceArena() {

		Gate arenaGate = DaoManager.I.getGatesDao().findByGateAdress("arena");
		List<NaniteGroup> naniteGroupsNearGate = DaoManager.I.getNanitesDao().findByEnvironment(
						arenaGate.getPosition().getEnvironment());
		HashSet<String> parties = new HashSet<String>();
		for (NaniteGroup naniteGroup : naniteGroupsNearGate) {
			if (!parties.contains(naniteGroup.getController().getName())) {
				parties.add(naniteGroup.getController().getName());
			}

		}

		if (arenaGate.getGatePass() == null) {
			if (Math.random() * 10 <= 1 && parties.size() >= 5) {

				arenaGate.setGatePass("" + Math.random());
				DaoManager.I.getGatesDao().update(arenaGate, true);
				new NaniteBPO().untrenchArena(naniteGroupsNearGate);
				L.info("arena locked");
			}
		} else if (naniteGroupsNearGate.size() == 1) {
			NaniteGroup winner = naniteGroupsNearGate.get(0);
			winner.getController().raiseArenaWins();
			Gate elysiumGate = DaoManager.I.getGatesDao().findByGateAdress("elysium");
			winner.setPosition(elysiumGate.getPosition());
			DaoManager.I.getAvatarDao().update(winner.getController(), true);
			DaoManager.I.getNanitesDao().update(winner, true);

			new MessageBPO().send(
							winner,
							winner.getController(),
							"Wir haben ein Arena-Turnier gewonnen! Der Sieg wurde uns zugeschrieben und die Gruppe zum Elysium transportiert. Herzlichen Glückwunsch, wir sind die Größten!");
			arenaGate.setGatePass(null);
			DaoManager.I.getGatesDao().update(arenaGate, true);
			L.info("arena battle ended. Winner is " + winner.getController().getName());
		} else if (arenaGate.getGatePass() != null) {

			// as long the tournament hppens, we untrench all units...
			new NaniteBPO().untrenchArena(naniteGroupsNearGate);

			// ... and apply some decrease

			int randomIndex = (int) (naniteGroupsNearGate.size() * Math.random());

			NaniteGroup victim = naniteGroupsNearGate.get(randomIndex);

			// lose max 20% of Nanites, but a minmum of 100
			int nanitesToRemove = (int) Math.max(100, victim.getNaniteCount() * Math.random() * 0.2);

			if (nanitesToRemove >= victim.getNaniteCount()) {
				// uh,... move to elysium instead
				Gate elysiumGate = DaoManager.I.getGatesDao().findByGateAdress("elysium");
				victim.setPosition(elysiumGate.getPosition());
				new MessageBPO().send(victim, victim.getController(),
								"eine unbeschreibliche Macht hat uns gepackt und aus dem geschlossenen Arena-Planeten ins Elysium verschoben.");
			} else {
				victim.setNaniteCount(victim.getNaniteCount() - nanitesToRemove);
				new MessageBPO().send(victim, victim.getController(), "eine unbeschreibliche Macht hat uns "
								+ nanitesToRemove + " Naniten genommen");
			}

			DaoManager.I.getNanitesDao().update(victim, true);

		}
	}

	private void runNanitesAdvancer() {
		L.info("running nanites advancer ...");

		org.hibernate.classic.Session currentSession = sessionFactory.getCurrentSession();

		currentSession.getTransaction().begin();

		List<NaniteGroup> result = new LinkedList<NaniteGroup>();

		DaoManager.I.getNanitesDao().loadAll(result);
		int changeCount = 0;

		for (Iterator iterator = result.iterator(); iterator.hasNext();) {
			NaniteGroup naniteGroup = (NaniteGroup) iterator.next();

			boolean changedSomething = advanceEntrenching(naniteGroup);

			if (changedSomething) {
				changeCount++;
				currentSession.update(naniteGroup);
			}

		}

		currentSession.getTransaction().commit();

		L.info(changeCount + " groups advanced");
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

		if (naniteGroup.getStateEndGT().before(GalaxyTimer.get().getGalaxyDate())) {
			naniteGroup.setStateEndGT(null);
			naniteGroup.setState(State.ENTRENCHED);
			new MessageBPO().send(naniteGroup, naniteGroup.getController(), "Eingraben beendet.");
			return true;
		}
		L.debug("nanite group still has to wait until " + naniteGroup.getStateEndGT() + " ");
		return false;
	}
}
