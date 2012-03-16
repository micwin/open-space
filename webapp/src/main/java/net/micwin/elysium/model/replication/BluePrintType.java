package net.micwin.elysium.model.replication;

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