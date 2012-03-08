package net.micwin.elysium.jobs;

import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdvancerTask extends TimerTask {

	private static final Logger L = LoggerFactory.getLogger(AdvancerTask.class);

	@Override
	public void run() {
		L.info("running ...");
		L.info("done");
	}
}
