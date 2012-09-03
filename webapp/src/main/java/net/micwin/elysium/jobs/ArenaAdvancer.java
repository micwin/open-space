package net.micwin.elysium.jobs;

import java.util.HashSet;
import java.util.List;

import net.micwin.elysium.bpo.MessageBPO;
import net.micwin.elysium.bpo.NaniteBPO;
import net.micwin.elysium.dao.DaoManager;
import net.micwin.elysium.entities.characters.Avatar;
import net.micwin.elysium.entities.gates.Gate;
import net.micwin.elysium.entities.nanites.NaniteGroup;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ArenaAdvancer {

	private static final Log L = LogFactory.getLog(ArenaAdvancer.class);

	public void advance() {
		L.info("advancing arena ...");

		Gate arenaGate = DaoManager.I.getGatesDao().findByGateAdress("arena");
		List<NaniteGroup> naniteGroupsNearGate = DaoManager.I.getNanitesDao().findByEnvironment(
						arenaGate.getPosition().getEnvironment());
		HashSet<String> parties = new HashSet<String>();
		for (NaniteGroup naniteGroup : naniteGroupsNearGate) {
			if (!parties.contains(naniteGroup.getController().getName())) {
				parties.add(naniteGroup.getController().getName());
			}
		}

		L.debug("parties on arena: " + parties.size() + ' ' + parties);

		if (arenaGate.getGatePass() == null) {
			startGames(arenaGate, naniteGroupsNearGate, parties);
		} else if (parties.size() == 1) {

			endGamesWithVictory(arenaGate, naniteGroupsNearGate);
		} else if (naniteGroupsNearGate.size() < 1) {
			endgamesWithoutWinner(arenaGate);

		} else {

			advanceGames(naniteGroupsNearGate);
		}
		L.debug("arena advancement done");
	}

	public void advanceGames(List<NaniteGroup> naniteGroupsNearGate) {
		L.debug("another tournament round in the arena");

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
			L.info(victim + " transferred from closed arena to elysium");
		} else {
			victim.setNaniteCount(victim.getNaniteCount() - nanitesToRemove);
			new MessageBPO().send(victim, victim.getController(), "eine unbeschreibliche Macht hat uns "
							+ nanitesToRemove + " Naniten genommen. Wir haben jetzt noch " + victim.getNaniteCount());
			L.info("removed " + nanitesToRemove + " nanites from group " + victim);

		}
		DaoManager.I.getNanitesDao().update(victim);
	}

	public void endgamesWithoutWinner(Gate arenaGate) {
		L.info("ending arena battle without winner");
		arenaGate.setGatePass(null);
		DaoManager.I.getGatesDao().update(arenaGate);
	}

	public void endGamesWithVictory(Gate arenaGate, List<NaniteGroup> naniteGroupsNearGate) {
		// victory condition, so all groups on arena belong to the same
		// winner
		L.info("determining victory");
		Avatar winner = naniteGroupsNearGate.get(0).getController();
		winner.raiseArenaWins();
		Gate elysiumGate = DaoManager.I.getGatesDao().findByGateAdress("elysium");
		for (NaniteGroup group : naniteGroupsNearGate) {
			winner.setPosition(elysiumGate.getPosition());
			DaoManager.I.getNanitesDao().update(group);

		}

		DaoManager.I.getAvatarDao().update(winner);
		if (winner.getController() != null) {
			new MessageBPO().send(
							winner,
							winner.getController(),
							"Wir haben ein Arena-Turnier gewonnen! Der Sieg wurde uns zugeschrieben und die Gruppe zum Elysium transportiert. Herzlichen Glückwunsch, wir sind die Größten!");
		}
		arenaGate.setGatePass(null);
		DaoManager.I.getGatesDao().update(arenaGate);
		L.info("arena battle ended. Winner is "
						+ (winner.getController() != null ? winner.getController().getName() : "null"));
	}

	public void startGames(Gate arenaGate, List<NaniteGroup> naniteGroupsNearGate, HashSet<String> parties) {
		if (Math.random() * 10 <= 1 && parties.size() >= 5) {
			L.debug("locking arena ...");
			arenaGate.setGatePass("" + Math.random());
			DaoManager.I.getGatesDao().update(arenaGate);
			new NaniteBPO().untrenchArena(naniteGroupsNearGate);
			L.info("arena lockled now. Let the games begin!");
		} else {
			L.debug("too few parties on arena. Doing nothing.");
		}
	}

}
