package net.micwin.elysium.dao;

import java.io.Serializable;
import java.util.List;

import net.micwin.elysium.model.characters.User;
import net.micwin.elysium.model.characters.User.Role;
import net.micwin.elysium.model.characters.User.State;

import org.hibernate.Session;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class HibernateUserDao extends ElysiumHibernateDaoSupport<User> implements IUserDao {

	public HibernateUserDao() {
	}

	@Override
	public User getUser(String login, String pass) {

		List<User> result = getSession().createQuery(" from User where login='" + login + "' and pass='" + pass + "'")
						.list();

		if (result == null || result.size() < 1) {
			return null;
		} else {
			return (User) result.get(0);
		}

	}

	@Override
	public User create(String login, String pass, State state, Role role) {
		User user = new User(login, pass, state, role);
		getHibernateTemplate().save(user);
		return user;
	}

	@Override
	public User findByLogin(String login) {
		List<User> result = getSession().createQuery(" from User where login='" + login + "'").list();
		if (result.size() < 1) {
			return null;

		} else
			return result.get(0);
	}

	@Override
	public User loadById(Serializable id) {
		return super.loadById((Long) id);
	}

	@Override
	public Class<User> getEntityClass() {
		return User.class;
	}

}
