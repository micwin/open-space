
package net.micwin.elysium.bpo;

import net.micwin.elysium.model.characters.Avatar.Personality;

/**
 * The bpo that does xp related processes.
 * 
 * @author MicWin
 * 
 */
public class XpBPO extends BaseBPO {

	public XpBPO() {
	}

	/**
	 * The xp needed to got to the specified level.
	 * 
	 * @param level
	 * @return
	 */
	public long computeXpForLevel(int level, Personality personality) {
		switch (personality) {
		// case MILITARY:
		// return (long) Math.pow(12, level);
		// case PRESERVER:
		// return (long) Math.pow(11, level);
		case ENGINEER:
			return (long) Math.pow(10, level);
		default:
			throw new IllegalStateException("forgot to handle personality '" + personality.name() + "'");
		}
	}
}
