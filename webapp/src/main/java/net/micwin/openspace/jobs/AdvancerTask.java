package net.micwin.openspace.jobs;

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

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.TimerTask;

import net.micwin.openspace.bpo.MessageBPO;
import net.micwin.openspace.dao.DaoManager;
import net.micwin.openspace.entities.nanites.NaniteGroup;
import net.micwin.openspace.tools.RuntimeExceptionShell;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class AdvancerTask extends TimerTask {

	private static final Logger L = LoggerFactory.getLogger(AdvancerTask.class);

	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public void run() {

		L.info("running @ " + new Date() + "...");

		Session session = sessionFactory.getCurrentSession();
		Transaction tx = session.beginTransaction();

		try {
			runNanitesAdvancer();

			runArenaAdvancer();

			runPublicGatesAdvancer();

			runNPCAdvancer();

			tx.commit();
		} catch (RuntimeException e) {
			L.error("swallowed runtime exception - bad bad bad. Some advancers may not have run.",
					e);
		} finally {
			if (tx.isActive())
				tx.rollback();
		}

		L.info("done");
	}

	private void runNPCAdvancer() {
		new RuntimeExceptionShell() {

			@Override
			protected void doIt() {
				new NPCAdvancer().advance();
			}
		}.run();
	}

	private void runPublicGatesAdvancer() {
		new RuntimeExceptionShell() {

			@Override
			protected void doIt() {
				new PublicGatesAdvancer().advance();
			}
		}.run();
	}

	private void runArenaAdvancer() {
		new RuntimeExceptionShell() {

			@Override
			protected void doIt() {
				new ArenaAdvancer().advance();

			}
		}.run();
	}

	private void runNanitesAdvancer() {
		new RuntimeExceptionShell() {

			@Override
			protected void doIt() {
				new NanitesAdvancer().advance();
			}
		}.run();

	}

}
