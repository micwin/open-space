package net.micwin.openspace.entities.characters;

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

import net.micwin.openspace.entities.OpenSpaceEntity;

@Entity
public final class Organization extends OpenSpaceEntity {

	/**
	 * Creates a new Organization. Dont forget to set Controller before
	 * persisting.
	 * 
	 * @param longName
	 * @param abbreviation
	 * @return
	 */
	public static Organization create(String longName, String abbreviation) {

		// check lengths
		if (abbreviation.length() < 2) {
			throw new IllegalArgumentException("abbreviation '" + abbreviation + "' too short");
		} else if (abbreviation.length() > 3) {
			throw new IllegalArgumentException("abbreviation '" + abbreviation + "' too long");
		}
		Organization organization = new Organization();
		organization.longName = longName;
		organization.abbreviation = abbreviation;
		return organization;
	}

	@Column(unique = true)
	private String longName;

	@Column(unique = true)
	private String abbreviation;

	@Override
	public Class getBaseClass() {
		return Organization.class;
	}

	public void setLongName(String longName) {
		this.longName = longName;
	}

	public String getLongName() {
		return longName;
	}

	public void setAbbreviation(String abbreviation) {
		this.abbreviation = abbreviation;
	}

	public String getAbbreviation() {
		return abbreviation;
	}

}
