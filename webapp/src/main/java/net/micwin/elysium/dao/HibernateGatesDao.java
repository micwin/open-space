package net.micwin.elysium.dao;

import java.util.Collection;
import java.util.List;

import net.micwin.elysium.model.characters.Avatar;
import net.micwin.elysium.model.galaxy.Environment;
import net.micwin.elysium.model.gates.Gate;

public class HibernateGatesDao extends ElysiumHibernateDaoSupport<Gate> implements IGatesDao {

	public HibernateGatesDao() {
	}

	@Override
	public Collection<Gate> findByEnvironment(Environment environment) {
		List<Gate> result = lookupHql(" from Gate where position.environment.id=" + environment.getId());
		return result;
	}

	@Override
	public Class<Gate> getEntityClass() {
		return Gate.class;
	}
}