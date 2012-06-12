package net.micwin.elysium.dao;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import net.micwin.elysium.entities.appliances.Utilization;
import net.micwin.elysium.entities.characters.Race;

public class HibernateTalentsDao extends ElysiumHibernateDaoSupport<Utilization> implements ITalentsDao {

	public HibernateTalentsDao() {
	}

	@Override
	public Class<Utilization> getEntityClass() {
		return Utilization.class;
	}

	@Override
	public void saveAll(Iterable<Utilization> elements) {
		super.saveAll(elements);
	}

	@Override
	public Collection<Utilization> createInitialTalents(Race race) {

		Utilization[] initialTalents = race.getInitialTalents();
		List<Utilization> newTalents = new LinkedList<Utilization>();

		for (int i = 0; i < initialTalents.length; i++) {
			Utilization initialTalent = initialTalents[i];
			newTalents.add(Utilization.Factory.create(initialTalent.getAppliance(), initialTalent.getLevel()));
		}

		
		getHibernateTemplate().saveOrUpdateAll(newTalents);

		getHibernateTemplate().flush();
		return newTalents;

	}
}
