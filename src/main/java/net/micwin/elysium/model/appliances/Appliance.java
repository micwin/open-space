package net.micwin.elysium.model.appliances;

/**
 * An application is a certain use or action or ability an Avatar / NPC or an
 * object can do.
 * 
 * @author MicWin
 * 
 */
public enum Appliance {

	/**
	 * Designing buildings. Architecture defines how complex buildings may be,
	 * that is, how many modules (Utilizations of appliances) may be part of a
	 * building.
	 */
	ARCHITECTURE(1),

	/**
	 * Building living rooms for Avatars, NPCs and PCs.
	 */
	HABITATS(1);

	private final int baseComplexity;

	/**
	 * The complexity that one unit / component of level 1 of this appliance
	 * has.
	 * 
	 * @return
	 */

	private Appliance(int baseComplexity) {
		this.baseComplexity = baseComplexity;

	}

	public int getBaseComplexity() {
		return baseComplexity;
	}

}
