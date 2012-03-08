package net.micwin.elysium.dao;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import net.micwin.elysium.model.appliances.Utilization;
import net.micwin.elysium.model.characters.Avatar;
import net.micwin.elysium.model.characters.Avatar.Personality;
import net.micwin.elysium.model.characters.Organization;
import net.micwin.elysium.model.characters.User;
import net.micwin.elysium.model.galaxy.Position;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HibernateAvatarDao extends ElysiumHibernateDaoSupport<Avatar> implements IAvatarDao {

	private static final Logger L = LoggerFactory.getLogger(HibernateAvatarDao.class);

	public HibernateAvatarDao() {
	}

	@Override
	public Avatar findByController(User user) {
		if (L.isDebugEnabled()) {
			L.debug("looking for avatar of user " + user);
		}

		if (user == null) {
			return null;
		}

		List<Avatar> result = lookupHql(" from Avatar where controller.id=" + user.getId());

		if (L.isDebugEnabled()) {
			L.debug("result: " + result);
		}
		if (result == null || result.size() != 1) {
			return null;
		} else {
			return (Avatar) result.get(0);
		}
	}

	@Override
	public Avatar create(User user, String name, Personality personality, Collection<Utilization> talents,
					int talentPoints, Position position, Date birthDate) {
		Avatar avatar = new Avatar();
		avatar.setController(user);
		avatar.setName(name);
		avatar.setPersonality(personality);
		avatar.setCreationDate(birthDate);
		avatar.setLevel(0);
		avatar.setXp(0l);
		avatar.setTalentPoints(talentPoints);
		avatar.setPosition(position);
		avatar.setStoryLineItem(personality.getFirstStoryItem());

		Organization organization = new Organization();
		organization.setController(avatar);
		avatar.setOrganization(organization);
		getHibernateTemplate().save(organization);

		getHibernateTemplate().saveOrUpdateAll(talents);
		avatar.setTalents(talents);

		save(avatar);
		getHibernateTemplate().flush();

		return avatar;
	}

	@Override
	public boolean nameExists(String name) {
		Avatar avatar = new Avatar();
		avatar.setName(name);
		return getHibernateTemplate().findByExample(avatar).size() > 0;
	}

	@Override
	public Class<Avatar> getEntityClass() {
		return Avatar.class;
	}

	@Override
	public Avatar loadById(Serializable id) {
		return super.loadById((Long) id);
	}
}
