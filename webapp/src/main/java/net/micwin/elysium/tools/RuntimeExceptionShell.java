package net.micwin.elysium.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A class that catches uncaught runtime exceptions and drops them to the log.
 * 
 * @author MicWin
 * 
 */
public abstract class RuntimeExceptionShell implements Runnable {

	private static final Logger L = LoggerFactory
			.getLogger(RuntimeExceptionShell.class);

	private Class<? extends RuntimeException>[] exceptionsToSwallow;

	/**
	 * @param exceptionsToSwallow
	 *            optional. Base classes to swallow. If missing, all
	 *            RuntimeExceptions are caught.
	 * @param exceptionsToSwallow
	 */
	@SuppressWarnings("unchecked")
	public RuntimeExceptionShell(
			Class<? extends RuntimeException>... exceptionsToSwallow) {
		if (exceptionsToSwallow == null || exceptionsToSwallow.length == 0) {
			this.exceptionsToSwallow = new Class[] { RuntimeException.class };
		} else {
			this.exceptionsToSwallow = exceptionsToSwallow;
		}
	}

	/**
	 * The method to be overridden as an anonymous adapter to ensure the runtime
	 * exception does not go further.
	 * 
	 * @return
	 */
	protected abstract void doIt();

	@Override
	public void run() {
		try {
			doIt();
		} catch (RuntimeException rte) {
			for (Class<? extends RuntimeException> candidate : exceptionsToSwallow) {

				if (candidate.isInstance(rte)) {
					L.warn("swallowing exception", rte);
				} else {
					// propagate
					throw rte;
				}
			}
		}
	}
}
