package net.micwin.openspace.jee;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import net.micwin.openspace.entities.DatabaseConsistencyEnsurer;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenSpaceFilter implements Filter {

	private static final Logger L = LoggerFactory.getLogger(OpenSpaceFilter.class);

	private SessionFactory sf;

	private boolean sessionTxEnabled = true;

	private DatabaseConsistencyEnsurer dbConsistencyEnsurer;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

		L.info("initialized. sessionTxEnabled=" + getSessionTxEnabled());
	}

	public void setSf(SessionFactory sf) {
		this.sf = sf;
	}

	public SessionFactory getSf() {
		return sf;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
					ServletException {

		L.debug("incoming ...");

		if (dbConsistencyEnsurer != null) {
			dbConsistencyEnsurer.ensureDatabaseConsistency();
			dbConsistencyEnsurer = null;
		}

		if (sessionTxEnabled) {

			doSessionTxEnabled(request, response, chain);
		}

		else {

			chain.doFilter(request, response);
		}
		L.debug("... done.");

	}

	public void doSessionTxEnabled(ServletRequest request, ServletResponse response, FilterChain chain)
					throws IOException, ServletException {
		Session session = sf.getCurrentSession();
		L.debug("opened session");
		Transaction tx = session.beginTransaction();
		L.debug("opened transaction");
		try {

			chain.doFilter(request, response);
			if (tx.isActive())
				tx.commit();
			L.debug("tx committed");
		} finally {
			if (tx.isActive()) {
				tx.rollback();
				L.debug("rolled back");
			}

		}
	}

	@Override
	public void destroy() {

		L.info("destroyed.");

	}

	public void setSessionTxEnabled(boolean sessionTxEnabled) {
		this.sessionTxEnabled = sessionTxEnabled;
	}

	public boolean getSessionTxEnabled() {
		return sessionTxEnabled;
	}

	public void setDbConsistencyEnsurer(DatabaseConsistencyEnsurer dbConsistencyEnsurer) {
		this.dbConsistencyEnsurer = dbConsistencyEnsurer;
	}

	public DatabaseConsistencyEnsurer getDbConsistencyEnsurer() {
		return dbConsistencyEnsurer;
	}

}
