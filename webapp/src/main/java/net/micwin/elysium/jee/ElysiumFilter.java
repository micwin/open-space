package net.micwin.elysium.jee;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ElysiumFilter implements Filter {

	private static final Logger L = LoggerFactory.getLogger(ElysiumFilter.class);

	private SessionFactory sf;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		L.info("initialized");
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
					ServletException {

		try {

			chain.doFilter(request, response);

		} finally {
		}
	}

	@Override
	public void destroy() {

		L.info("destroyed.");

	}

	public void setSf(SessionFactory sf) {
		this.sf = sf;
	}

	public SessionFactory getSf() {
		return sf;
	}

}
