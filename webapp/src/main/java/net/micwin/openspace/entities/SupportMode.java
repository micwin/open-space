package net.micwin.openspace.entities;

/**
 * Specifies on which way an entity assists if an own unit or a unit of a buddy
 * or ally attacks or gets attacked.
 * 
 * @author MicWin
 * 
 */
public enum SupportMode {

	/**
	 * Do not enforce.
	 */
	NONE,

	/**
	 * Adds to the attack strength of own attacking forces in the same
	 * environment.
	 */
	ENFORCE_ENVIRONMENT_OWN;

}
