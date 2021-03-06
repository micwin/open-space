package net.micwin.openspace.entities.nanites;

public enum NaniteState {

	IDLE(1.0, 1.0, 1.0, 1.0, true, 1.0, true, true, true), ENTRENCHING(0.0, 0.0, 1.1, 0.5, false, 0.9, true, false,
					false), ENTRENCHED(0.1, 0.1, 0.2, 10, false, 0.2, true, true, false), UPGRADING(0.0, 0.0, 1.1, 0.5,
					false, 1.1, true, true, true), PASSIVATED(0.0, 0.0, 0.0, 0.0, false, 3, true, false, false);

	final double attackDamageFactor;
	final double counterStrikeDamageFactor;
	final double receivingDamageFactor;

	final double signatureFactor;
	final double sensorFactor;

	final boolean mayAttack;
	final boolean canRaiseNanitesCount;
	private boolean canSplit;

	private boolean canReform;

	private NaniteState(double pAttackDamageFactor, double pCounterStrikeFactor, double pSignatureFactor,
					double pSensorFactor, boolean pMayAttack, double pReceivingDamageFactor,
					boolean pCanRaisenanitesCount, boolean pCanSplit, boolean pCanReform) {
		this.attackDamageFactor = pAttackDamageFactor;
		this.counterStrikeDamageFactor = pCounterStrikeFactor;
		this.signatureFactor = pSignatureFactor;
		this.sensorFactor = pSensorFactor;
		this.mayAttack = pMayAttack;
		this.receivingDamageFactor = pReceivingDamageFactor;
		this.canRaiseNanitesCount = pCanRaisenanitesCount;
		this.canSplit = pCanSplit;
		canReform = pCanReform;
	}

	public double getAttackDamageFactor() {
		return attackDamageFactor;
	}

	public double getCounterStrikeDamageFactor() {
		return counterStrikeDamageFactor;
	}

	public double getSignatureFactor() {
		return signatureFactor;
	}

	public double getSensorFactor() {
		return sensorFactor;
	}

	public boolean mayAttack() {
		return mayAttack;
	}

	public double getReceivingDamageFactor() {
		return receivingDamageFactor;
	}

	public boolean canRaiseNanitesCount() {
		return canRaiseNanitesCount;
	}

	public boolean canSplit() {

		return canSplit;
	}

	public boolean canReform() {
		return canReform;
	}
}