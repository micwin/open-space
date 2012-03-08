package net.micwin.elysium.dao;

import net.micwin.elysium.model.SysParam;

public interface ISysParamDao extends IElysiumEntityDao<SysParam> {

	/**
	 * Returns the sysparam that has the specified key. if not found, create a
	 * key with the specified default (and return this one).
	 * 
	 * @param key
	 * @param defaultValue
	 * @return the value that fits to the specified key, or
	 *         <code>defaultValue</code>.
	 */
	SysParam findByKey(String key, String defaultValue);

	/**
	 * Creates or updates the sysparam with the specified key.
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public SysParam create(String key, String value);
}
