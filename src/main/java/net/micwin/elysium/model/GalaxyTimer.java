package net.micwin.elysium.model;

import java.util.Date;

/**
 * A timer that manages the time of the simulated galaxy.
 * 
 * @author MicWin
 * 
 */
public class GalaxyTimer {

	private long realTimeStartMillis;
	private long galaxyTimeStartMillis;

	public GalaxyTimer(long galaxyStartMillis) {
		this.galaxyTimeStartMillis = galaxyStartMillis;
		realTimeStartMillis = System.currentTimeMillis();
	}

	public Date getGalaxyDate() {
		return new Date(galaxyTimeStartMillis - realTimeStartMillis + System.currentTimeMillis());
	}
}
