package net.micwin.openspace.dao.hibernate;

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

import net.micwin.openspace.dao.IMessageDao;
import net.micwin.openspace.dao.OpenSpaceHibernateDaoSupport;
import net.micwin.openspace.entities.characters.Avatar;
import net.micwin.openspace.entities.messaging.Message;
import net.micwin.openspace.messaging.IMessageEndpoint;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HibernateMessageDao extends OpenSpaceHibernateDaoSupport<Message> implements IMessageDao {

	protected HibernateMessageDao(SessionFactory sf) {
		super(sf);
	}

	private static final Logger L = LoggerFactory.getLogger(HibernateMessageDao.class);

	@Override
	public Class<Message> getEntityClass() {
		return Message.class;
	}

	@Override
	public List<Message> findByEndPoint(IMessageEndpoint endPoint) {

		StringBuffer query = new StringBuffer();
		query.append("from ").append(Message.class.getSimpleName());
		query.append(" where mailBox='").append(endPoint.getEndPointId() + "'");
		return lookupHql(query.toString());
	}

	@Override
	public void send(Message message) {
		update(message);
	}

	@Override
	public boolean hasNewMessages(Avatar avatar) {
		String query = " from " + Message.class.getSimpleName() + " where viewedDate is null and mailBox='"
						+ avatar.getEndPointId() + "'";

		return lookupHql(query).size() > 0;
	}

}
