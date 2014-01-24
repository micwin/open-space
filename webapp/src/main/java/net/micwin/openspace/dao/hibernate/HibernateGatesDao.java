package net.micwin.openspace.dao.hibernate;

import java.util.Collection;
import java.util.List;
import java.util.Random;

import net.micwin.openspace.dao.IGatesDao;
import net.micwin.openspace.dao.OpenSpaceHibernateDaoSupport;
import net.micwin.openspace.entities.galaxy.Position;
import net.micwin.openspace.entities.gates.Gate;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HibernateGatesDao extends OpenSpaceHibernateDaoSupport<Gate> implements IGatesDao {

	protected HibernateGatesDao(SessionFactory sf) {
		super(sf);
	}

	private static final Logger L = LoggerFactory.getLogger(HibernateGatesDao.class);

	

	@Override
	public Class<Gate> getEntityClass() {
		return Gate.class;
	}

	@Override
	public Gate create(Position position) {
		Gate newGate = new Gate();
		newGate.setPosition(position);
		newGate.setGateAdress(createUniqueGateAdress());
		super.insert(newGate);
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