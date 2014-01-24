package net.micwin.openspace.dao.hibernate;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import net.micwin.openspace.dao.ITalentsDao;
import net.micwin.openspace.dao.OpenSpaceHibernateDaoSupport;
import net.micwin.openspace.entities.appliances.Utilization;
import net.micwin.openspace.entities.characters.Race;

import org.hibernate.SessionFactory;

public class HibernateTalentsDao extends OpenSpaceHibernateDaoSupport<Utilization> implements ITalentsDao {

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

		update(newTalents);
		return newTalents;

	}
}
