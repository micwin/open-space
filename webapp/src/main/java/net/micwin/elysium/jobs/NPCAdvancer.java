package net.micwin.elysium.jobs;

import java.util.Collection;
import java.util.LinkedList;

import net.micwin.elysium.dao.DaoManager;
import net.micwin.elysium.entities.NaniteGroup;
import net.micwin.elysium.entities.NaniteGroup.State;
import net.micwin.elysium.entities.appliances.Utilization;
import net.micwin.elysium.entities.characters.Avatar;
import net.micwin.elysium.entities.characters.User;
import net.micwin.elysium.entities.gates.Gate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Advances the various NPCs.
 * 
 * @author MicWin
 * 
 */
public class NPCAdvancer {

	private static final int NPC_GROUPS_ON_ARENA = 15;
	public static final String NAME_AI_0 = "blackw00d-AI";

	private static final Logger L = LoggerFactory.getLogger(NPCAdvancer.class);

	public void advance() {

		advance(NAME_AI_0);

	}

	private void advance(String aiName) {
		User user = DaoManager.I.getUserDao().findByLogin(NAME_AI_0);
		Avatar avatar = DaoManager.I.getAvatarDao().findByUser(user);

		L.info("advancing NPC " + avatar.getName());

		floodArena(avatar);
		clickSome(avatar, 5);
	}

	/**
	 * Simulates some random clicking
	 */
	private void clickSome(Avatar avatar, int clickCount) {

		LinkedList<Utilization> talents = new LinkedList<Utilization>(avatar.getTalents());

		for (int i = 0; i < clickCount; i++) {
			int index = (int) (Math.random() * talents.size());
			Utilization util = talents.get(index);
			util.raiseCount();
			DaoManager.I.getTalentsDao().update(util);
		}

	}

	private void floodArena(Avatar avatar) {

		Gate arenaGate = DaoManager.I.getGatesDao().findByGateAdress("arena");

		if (arenaGate.getGatePass() != null) {
			L.info("not flooding arena - closed");

			return;
		} else
			L.info("flooding arena");

		// otherwise,...

		Collection<NaniteGroup> onArena = getNanitesOnArena(avatar);

		int toCreate = NPC_GROUPS_ON_ARENA - onArena.size();
		while (toCreate > 0) {
			long count = (long) (Math.random() * 1000 * avatar.getLevel());

			NaniteGroup newGroup = DaoManager.I.getNanitesDao().create(count, arenaGate.getPosition());
			avatar.getNanites().add(newGroup);
			newGroup.setController(avatar);
			DaoManager.I.getNanitesDao().update(newGroup);
			toCreate--;
		}
		DaoManager.I.getAvatarDao().update(avatar);

	}

	public Collection<NaniteGroup> getNanitesOnArena(Avatar avatar) {
		Gate arenaGate = DaoManager.I.getGatesDao().findByGateAdress("arena");
		Collection<NaniteGroup> onArena = new LinkedList<NaniteGroup>();
		for (NaniteGroup naniteGroup : avatar.getNanites()) {
			if (naniteGroup.getState() != State.IDLE) {
				continue;
			}

			if (naniteGroup.getPosition().getEnvironment().equals(arenaGate.getPosition().getEnvironment())) {
				onArena.add(naniteGroup);

			}
		}

		L.debug("having " + onArena.size() + " groups on arena");
		return onArena;
	}
}
