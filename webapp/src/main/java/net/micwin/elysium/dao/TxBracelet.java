package net.micwin.elysium.dao;

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
	private boolean createSessionOnDemand = false;

	public TxBracelet(SessionFactory sf) {
		this(sf, false);
	}

	public TxBracelet(SessionFactory sf, boolean createSessionOnDemand) {
		this.sf = sf;
		this.createSessionOnDemand = createSessionOnDemand;
	}

	/**
	 * Call this to execute your bunch of work.
	 */
	public final T execute() {

		Session session = createSessionOnDemand ? sf.openSession() : sf.getCurrentSession();
		Transaction tx = session.beginTransaction();
		T rv = null;
		try {
			rv = doWork(session, tx);
			if (createSessionOnDemand && tx.isActive()) {
				tx.commit();
			}
		} catch (Exception e) {
			L.error("cannot do work", e);
			if (tx.isActive()) {
				tx.rollback();
			}
			if (e instanceof RuntimeException)
				throw (RuntimeException) e;
		} finally {

			if (createSessionOnDemand && session.isOpen()) {
				session.close();
			}
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
