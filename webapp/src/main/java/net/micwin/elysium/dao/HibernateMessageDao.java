package net.micwin.elysium.dao;

/*
 (c) 2012 micwin.net

 This file is part of open-space.

 open-space is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 open-space is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero Public License for more details.

 You should have received a copy of the GNU Affero Public License
 along with open-space.  If not, see http://www.gnu.org/licenses.

 Diese Datei ist Teil von open-space.

 open-space ist Freie Software: Sie können es unter den Bedingungen
 der GNU Affero Public License, wie von der Free Software Foundation,
 Version 3 der Lizenz oder (nach Ihrer Option) jeder späteren
 veröffentlichten Version, weiterverbreiten und/oder modifizieren.

 open-space wird in der Hoffnung, dass es nützlich sein wird, aber
 OHNE JEDE GEWÄHRLEISTUNG, bereitgestellt; sogar ohne die implizite
 Gewährleistung der MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK.
 Siehe die GNU Affero Public License für weitere Details.

 Sie sollten eine Kopie der GNU Affero Public License zusammen mit diesem
 Programm erhalten haben. Wenn nicht, siehe http://www.gnu.org/licenses. 

 */
import java.util.List;

import net.micwin.elysium.entities.messaging.Message;
import net.micwin.elysium.messaging.IMessageEndpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HibernateMessageDao extends ElysiumHibernateDaoSupport<Message> implements IMessageDao {

	private static final Logger L = LoggerFactory.getLogger(HibernateMessageDao.class);

	public HibernateMessageDao() {
	}

	@Override
	public Class<Message> getEntityClass() {
		return Message.class;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Message> find(IMessageEndpoint sender, IMessageEndpoint receiver, boolean filterViewed) {
		StringBuffer query = new StringBuffer("from " + Message.class.getSimpleName());

		if (sender != null || receiver != null || filterViewed) {
			query.append(" where ");
		}

		int partsCount = 0;
		if (sender != null) {
			query.append("senderID='").append(sender.getEndPointId()).append('\'');
			partsCount++;
		}

		if (receiver != null) {
			if (partsCount > 0) {
				query.append(" and ");
			}

			query.append("receiverID='").append(receiver.getEndPointId()).append('\'');
			partsCount++;
		}

		if (filterViewed) {

			if (partsCount > 0) {
				query.append(" and ");
			}

			query.append("viewedDate IS NULL");
		}
		if (L.isDebugEnabled()) {
			L.debug("querying with " + query);
		}
		List<Message> result = (List<Message>) getHibernateTemplate().find(query.toString());

		if (L.isDebugEnabled()) {
			L.debug("got results : " + result);
		}
		return result;

	}
}
