package net.micwin.elysium.model.galaxy;

/* This file is part of open-space.

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

 open-space ist Freie Software: Sie k�nnen es unter den Bedingungen
 der GNU Affero Public License, wie von der Free Software Foundation,
 Version 3 der Lizenz oder (nach Ihrer Option) jeder sp�teren
 ver�ffentlichten Version, weiterverbreiten und/oder modifizieren.

 open-space wird in der Hoffnung, dass es n�tzlich sein wird, aber
 OHNE JEDE GEW�HELEISTUNG, bereitgestellt; sogar ohne die implizite
 Gew�hrleistung der MARKTF�HIGKEIT oder EIGNUNG F�R EINEN BESTIMMTEN ZWECK.
 Siehe die GNU Affero Public License f�r weitere Details.

 Sie sollten eine Kopie der GNU Affero Public License zusammen mit diesem
 Programm erhalten haben. Wenn nicht, siehe http://www.gnu.org/licenses. 

 */

import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

@Entity
public class SolarSystem extends Environment {

	public SolarSystem() {
		super(SolarSystem.class);
	}

	@OneToMany(cascade = CascadeType.ALL)
	private List<Planet> planets = new LinkedList<Planet>();

	@OneToOne
	private Planet mainPlanet;

	public Planet getMainPlanet() {
		return mainPlanet;
	}

	public void setPlanets(List<Planet> planets) {
		this.planets = planets;
	}

	public List<Planet> getPlanets() {
		return planets;
	}

	public void setMainPlanet(Planet planet) {
		this.mainPlanet = planet;
	}

	@Override
	public String getName() {
		return "SolarSystem " + getPosition().getX() + "/" + getPosition().getY() + " @ "
						+ getPosition().getEnvironment().getName();
	}

}
