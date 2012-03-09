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

/**
 * A sector of space, spanning 100 x 100 lightyears.
 * 
 * @author MicWin
 * 
 */
@SuppressWarnings("rawtypes")
@Entity
public class Sector extends Environment {

	@OneToMany(cascade = CascadeType.ALL)
	private List<SolarSystem> solarSystems = new LinkedList<SolarSystem>();

	private int systemCount;

	public Sector() {
		super(Sector.class);
	}

	public void setSolarSystems(List<SolarSystem> solarSystems) {
		this.solarSystems = solarSystems;
		setSystemCount(solarSystems.size());
	}

	public List<SolarSystem> getSolarSystems() {
		return solarSystems;
	}

	public void addSolarSystem(SolarSystem solarSystem) {
		solarSystems.add(solarSystem);
		setSystemCount(solarSystems.size());

	}

	public void setSystemCount(int systemCount) {
		this.systemCount = systemCount;
	}

	public int getSystemCount() {
		return systemCount;
	}

	@Override
	public String getName() {
		return "Sector " + getPosition().getX() + "/" + getPosition().getY();
	}
}
