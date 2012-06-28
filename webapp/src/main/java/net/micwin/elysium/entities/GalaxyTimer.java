package net.micwin.elysium.entities;

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

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A timer that manages the time of the simulated galaxy.
 * 
 * @author MicWin
 * 
 */
public class GalaxyTimer {

	private static final Logger L = LoggerFactory.getLogger(GalaxyTimer.class);

	private static GalaxyTimer instance;

	public static GalaxyTimer get() {

		return instance;
	}

	public static void set(GalaxyTimer newTimer) {

		if (newTimer != null && instance != null) {
			throw new IllegalStateException("may not re-set galaxy timer singleton");
		}

		instance = newTimer;
		L.info("new galaxy Time " + instance.getGalaxyDate());

	}

	public static final long DEFAULT_OFFSET = 100 * 365 * 24 * 60 * 60 * 1000;

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
