package net.micwin.openspace.entities.galaxy;

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
import javax.persistence.Embedded;
import javax.persistence.MappedSuperclass;

import net.micwin.openspace.entities.OpenSpaceEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@MappedSuperclass
public abstract class Environment extends OpenSpaceEntity {

	private static final Logger L = LoggerFactory.getLogger(Environment.class);

	@Embedded
	private Position position;

	@Column(columnDefinition = "int default 0")
	private int width;

	@Column(columnDefinition = "int default 0")
	private int height;

	@Column(nullable = false, columnDefinition = "boolean default false")
	private boolean elysium = false;

	public void setWidth(int width) {
		this.width = width;
	}

	public int getWidth() {
		return width;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getHeight() {
		return height;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public Position getPosition() {
		return position;
	}

	public boolean isElysium() {
		return elysium;
	}

	public void setElysium(boolean newIsElysium) {
		elysium = newIsElysium;
	}

	public void setElysium(Boolean newIsElysium) {
		elysium = newIsElysium == null ? false : newIsElysium;
	}

	/**
	 * Returns wether or not elements contained by this environment must get
	 * passivated.
	 * 
	 * @return
	 */
	public abstract boolean needsPassivation();

}
