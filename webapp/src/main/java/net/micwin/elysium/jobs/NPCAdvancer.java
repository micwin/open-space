package net.micwin.elysium.jobs;

import java.util.Collection;
import java.util.LinkedList;

import net.micwin.elysium.dao.DaoManager;
import net.micwin.elysium.entities.NaniteGroup;
import net.micwin.elysium.entities.NaniteGroup.State;
import net.micwin.elysium.entities.characters.Avatar;
import net.micwin.elysium.entities.characters.User;
import net.micwin.elysium.entities.galaxy.Environment;
import net.micwin.elysium.entities.galaxy.Position;
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
		for (int i = 0; i < toCreate; i++) {
			long count = (long) (Math.random() * 1000 * avatar.getLevel());

			NaniteGroup newGroup = DaoManager.I.getNanitesDao().create(count, arenaGate.getPosition());
			avatar.getNanites().add(newGroup);
			newGroup.setController(avatar);
			DaoManager.I.getNanitesDao().update(newGroup);
		}
		L.info(toCreate + " nanitegroups created for npc " + avatar.getName() + " on arena");
		DaoManager.I.getAvatarDao().update(avatar);

	}

	public Collection<NaniteGroup> getNanitesOnArena(Avatar avatar) {
		Gate arenaGate = DaoManager.I.getGatesDao().findByGateAdress("arena");
		
		Collection<NaniteGroup> onArena =  DaoManager.I.getNanitesDao().findBy(avatar, arenaGate.getPosition().getEnvironment()) ; 
		
		L.debug("having " + onArena.size() + " groups on arena");
		return onArena;
	}
}
