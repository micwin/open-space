package net.micwin.elysium.model.replication;

import net.micwin.elysium.model.appliances.Appliance;
import net.micwin.elysium.model.galaxy.Planet;

/**
 * Specifies the type of a blueprint, ie holds applications and technology this
 * blueprint supplies (and requires to be built).
 * 
 * @author MicWin
 * 
 */
public enum BluePrintType {

	/**
	 * A building on a planet.
	 */
	PLANETARY_BASE(new Appliance[] { Appliance.ARCHITECTURE, Appliance.HABITATS }, new Class[] { Planet.class },
					new SimpleComplexityCalculator(10));

	private final Appliance[] coreAppliances;
	private final Appliance[] optionalAppliances;
	@SuppressWarnings("rawtypes")
	private final Class[] acceptedTargets;
	private final IComplexityCalculator complexityCalculator;

	private BluePrintType(Appliance[] coreAppliances, @SuppressWarnings("rawtypes") Class[] acceptedTargets,
					IComplexityCalculator complexityCalculator, Appliance... optionalAppliances) {
		this.coreAppliances = coreAppliances;
		this.optionalAppliances = optionalAppliances;
		this.acceptedTargets = acceptedTargets;
		this.complexityCalculator = complexityCalculator;
	}

	/**
	 * Retuirns wether or not this blueprint could be build to be installed in
	 * the specified target.
	 * 
	 * @param target
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean acceptsTarget(Class target) {

		for (Class acceptedTarget : acceptedTargets) {
			if (target.isAssignableFrom(acceptedTarget)) {
				return true;
			}
		}
		return false;
	}

	public IComplexityCalculator getComplexityCalculator() {
		return complexityCalculator;
	}

	public Appliance[] getCoreAppliances() {
		return coreAppliances;
	}

	public Appliance[] getOptionalAppliances() {
		return optionalAppliances;
	}

	public Class[] getAcceptedTargets() {
		return acceptedTargets;
	}
}