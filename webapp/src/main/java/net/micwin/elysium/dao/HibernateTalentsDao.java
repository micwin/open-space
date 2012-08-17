package net.micwin.elysium.dao;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.hibernate.SessionFactory;

import net.micwin.elysium.entities.appliances.Utilization;
import net.micwin.elysium.entities.characters.Race;

public class HibernateTalentsDao extends ElysiumHibernateDaoSupport<Utilization> implements ITalentsDao {

	public HibernateTalentsDao(SessionFactory sf) {
		super(sf);
	}

	@Override
	public Class<Utilization> getEntityClass() {
		return Utilization.class;
	}

	@Override
	public Collection<Utilization> createInitialTalents(Race race) {

		Utilization[] initialTalents = race.getInitialTalents();
		List<Utilization> newTalents = new LinkedList<Utilization>();

		for (int i = 0; i < initialTalents.length; i++) {
			Utilization initialTalent = initialTalents[i];
			newTalents.add(Utilization.Factory.create(initialTalent.getAppliance(), initialTalent.getLevel(),
							initialTalent.getMaxLevel()));
		}

		update(newTalents, true);
		return newTalents;

	}
}
