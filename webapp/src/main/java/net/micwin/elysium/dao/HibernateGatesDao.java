package net.micwin.elysium.dao;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import net.micwin.elysium.entities.galaxy.Environment;
import net.micwin.elysium.entities.galaxy.Position;
import net.micwin.elysium.entities.gates.Gate;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HibernateGatesDao extends ElysiumHibernateDaoSupport<Gate> implements IGatesDao {

	protected HibernateGatesDao(SessionFactory sf) {
		super(sf);
	}

	private static final Logger L = LoggerFactory.getLogger(HibernateGatesDao.class);

	@Override
	public Collection<Gate> findByEnvironment(Environment environment) {
		List<Gate> resultSet = lookupHql(" from Gate where position.environment.id=" + environment.getId());

		LinkedList<Gate> result = new LinkedList<Gate>(resultSet);
		return result;
	}

	@Override
	public Class<Gate> getEntityClass() {
		return Gate.class;
	}

	@Override
	public Gate create(Position position) {
		Gate newGate = new Gate();
		newGate.setPosition(position);
		newGate.setGateAdress(createUniqueGateAdress());
		super.insert(newGate, true);
		if (L.isDebugEnabled()) {
			L.debug("created gate " + newGate.toString());
		}

		return newGate;

	}

	private String createUniqueGateAdress() {

		String code = null;

		Random r = new Random();

		while (code == null) {

			StringBuffer sb = new StringBuffer();
			sb.append(Integer.toHexString(r.nextInt()).substring(0, 4)).append('-');
			sb.append(Integer.toHexString(r.nextInt()).substring(0, 4)).append('-');
			sb.append(Integer.toHexString(r.nextInt()).substring(0, 4));
			code = sb.toString();

			// check wether this code is already used
			Gate gate = findByGateAdress(code);
			if (gate != null)
				code = null;

		}

		return code;
	}

	@Override
	public Gate findByGateAdress(String code) {

		List<Gate> result = lookupHql(" from Gate where gateAdress='" + code + "'");

		if (result.size() < 1)
			return null;
		return result.get(0);
	}

	@Override
	public Collection<String> findPublicGateAdresses() {
		String query = "select gateAdress from Gate where gateAdress not in (select homeGateAdress from Avatar)";

		return lookupHqlBare(query);
	}
}