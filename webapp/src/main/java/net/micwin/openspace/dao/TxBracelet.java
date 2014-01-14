package net.micwin.openspace.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A helper class to do some hibernate work the old style.
 * 
 * @author MicWin
 * 
 */
public abstract class TxBracelet<T> {

	private static final Logger L = LoggerFactory.getLogger(TxBracelet.class);

	private SessionFactory sf;
	
	public TxBracelet(SessionFactory sf) {
		this(sf, false);
	}

	public TxBracelet(SessionFactory sf, boolean createSessionOnDemand) {
		this.sf = sf;

	}

	/**
	 * Call this to execute your bunch of work.
	 */
	public final T execute() {

		Session session = sf.getCurrentSession();
		T rv = null;
		try {
			rv = doWork(session, session.getTransaction());
		} catch (Exception e) {
			L.error("cannot do work", e);
			if (e instanceof RuntimeException)
				throw (RuntimeException) e;
		}
		return rv;
	}

	/**
	 * Implement this and put your work in it. You dont have to care about
	 * transactions and sessions.
	 * 
	 * @param session
	 *            the session - ready to be used by you.
	 * @param tx
	 *            the opened transaction.
	 * @return
	 */
	public abstract T doWork(Session session, Transaction tx);
}
