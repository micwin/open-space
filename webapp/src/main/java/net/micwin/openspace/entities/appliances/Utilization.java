package net.micwin.openspace.entities.appliances;

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
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import net.micwin.openspace.entities.OpenSpaceEntity;

/**
 * A utilization is a knowledge of an appliance on a specific level.
 * 
 * @author MicWin
 * 
 */
@Entity
public final class Utilization extends OpenSpaceEntity {

	public static class Factory {

		/**
		 * Creates a new utilization instance. Does, of course, NOT persist!
		 * 
		 * @param appliance
		 * @param level
		 * @return
		 */
		public static Utilization create(Appliance appliance, int level, int maxLevel) {
			Utilization u = new Utilization();
			u.setAppliance(appliance);
			u.setLevel(level);
			u.setMaxLevel(maxLevel);
			return u;
		}
	};

	@Enumerated(EnumType.STRING)
	private Appliance appliance;

	@Column
	private int level;

	@Column(name = "USAGE_COUNT")
	private int count;

	@Column(name = "MAX_LEVEL")
	private int maxLevel;

	public Utilization() {
	}

	public void setLevel(int level) {
		this.level = level;
	}

	/**
	 * The level on which appliance is present.
	 * 
	 * @return
	 */
	public int getLevel() {
		return level;
	}

	public void setAppliance(Appliance appliance) {
		this.appliance = appliance;
	}

	/**
	 * The appliance to be known.
	 * 
	 * @return
	 */
	public Appliance getAppliance() {
		return appliance;
	}

	public void setCount(int count) {
		this.count = count;
	}

	/**
	 * how often the knowledge has been applied.
	 * 
	 * @return
	 */
	public int getCount() {
		return count;
	}

	public int raiseCount() {
		return ++count;
	}

	public void setMaxLevel(int maxLevel) {
		this.maxLevel = maxLevel;
	}

	/**
	 * The maximum level this utilization can have.
	 * 
	 * @return
	 */
	public int getMaxLevel() {
		return maxLevel;
	}

	@Override
	public Class getBaseClass() {
		return Utilization.class;
	}
}
