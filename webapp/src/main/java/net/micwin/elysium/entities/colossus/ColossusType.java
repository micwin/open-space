package net.micwin.elysium.entities.colossus;

/**
 * Specifies the design of a Colossus.
 * 
 * @author MicWin
 * 
 */
public enum ColossusType {

	/**
	 * A small artillery Colossus. Can travel through gates, easy built but not
	 * that tough.
	 */
	ARTY(1000, 100, 0, 2, 1, 100, 5);

	/**
	 * The build complexity on level 1.
	 */
	private final int baseComplexity;

	/**
	 * The number of base artillery in level 1.
	 */
	private final int baseArtillery;

	/**
	 * The speed the colossus can move.
	 */
	private final int movementSpeed;

	/**
	 * The number of repair stations at level 1.
	 */
	private final int baseRepairStations;

	/**
	 * The number of armor oints this colossus has on level 1
	 */
	private final int baseArmorPoints;

	/**
	 * The base signature this colossus has on level 1.
	 */
	private final int baseSignature;

	/**
	 * Thenumber of nanite groups this vehicle can transport at level 1.
	 */
	private final int baseTransportSlots;

	private ColossusType(int baseComplexity, int baseSignature, int baseTransportSlots, int baseArtilleryCount,
					int baseRepairStations, int baseArmorPoints, int movementSpeed) {
		this.baseComplexity = baseComplexity;
		this.baseSignature = baseSignature;
		this.baseTransportSlots = baseTransportSlots;
		this.baseArtillery = baseArtilleryCount;
		this.baseArmorPoints = baseArmorPoints;
		this.baseRepairStations = baseRepairStations;
		this.movementSpeed = movementSpeed;
	}

	public int getBaseComplexity() {
		return baseComplexity;
	}

	public int getBaseArtillery() {
		return baseArtillery;
	}

	public int getMovementSpeed() {
		return movementSpeed;
	}

	public int getBaseRepairStations() {
		return baseRepairStations;
	}

	public int getBaseArmorPoints() {
		return baseArmorPoints;
	}

	public int getBaseSignature() {
		return baseSignature;
	}

	public int getBaseNaniteGroupSlots() {
		return baseTransportSlots;
	}
}
