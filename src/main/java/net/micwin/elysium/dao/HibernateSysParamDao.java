package net.micwin.elysium.dao;

import java.io.Serializable;
import java.util.List;

import net.micwin.elysium.model.SysParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class HibernateSysParamDao extends ElysiumHibernateDaoSupport<SysParam> implements ISysParamDao {

	private static final Logger L = LoggerFactory.getLogger(HibernateSysParamDao.class);

	public HibernateSysParamDao() {
	}

	@Override
	public SysParam findByKey(String key, String defaultValue) {

		List<SysParam> result = getSession().createQuery(" from SysParam where key='" + key + "'").list();
		if (result.size() < 1) {
			return create(key, defaultValue);
		} else
			return result.get(0);
	}

	@Override
	public SysParam create(String key, String value) {
		SysParam sysParam = new SysParam();
		sysParam.setKey(key);
		sysParam.setValue(value);
		getHibernateTemplate().saveOrUpdate(sysParam);
		if (L.isDebugEnabled())
			L.debug("created sysparam key='" + sysParam.getKey() + "' value='" + sysParam.getValue() + "'");
		return sysParam;
	}

	@Override
	public Class<SysParam> getEntityClass() {
		return SysParam.class;
	}

	@Override
	public SysParam loadById(Serializable id) {
		return super.loadById((Long) id);
	}
}
