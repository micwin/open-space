package net.micwin.openspace.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A class that catches uncaught runtime exceptions, drops them to the log, and
 * then swallows. Usefulwhen there is a "dont care"-Condition like a finally
 * block that must guarantee that everything runs smoothly afterwards. Do not
 * use on a regularily basis! Keep in mind that, when no catched exception has
 * been defined, this shell also swallows NullPointerExceptions,
 * IllegalArgumentExceptions and everything else which really should get
 * corrected.
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

	/**
	 * The try ... catch shell around the call of doIt.
	 */
	@Override
	public final void run() {
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
