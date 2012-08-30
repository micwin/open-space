package net.micwin.elysium.entities.galaxy;

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

import java.util.LinkedList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;

import net.micwin.elysium.entities.nanites.NaniteGroup;

import org.hibernate.annotations.Any;
import org.hibernate.annotations.AnyMetaDef;
import org.hibernate.annotations.MetaValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A position of something in an environment.
 * 
 * @author MicWin
 * 
 */
@Embeddable
public class Position {

	private static final Logger L = LoggerFactory.getLogger(Position.class);

	@Any(metaColumn = @Column(name = "env_type"), fetch = FetchType.EAGER)
	@AnyMetaDef(idType = "long", metaType = "string", metaValues = {
					@MetaValue(value = "S", targetEntity = Sector.class),
					@MetaValue(value = "P", targetEntity = Planet.class),
					@MetaValue(value = "Y", targetEntity = SolarSystem.class),
					@MetaValue(value = "N", targetEntity = NaniteGroup.class)

	})
	@JoinColumn(name = "env_id")
	private Environment environment;

	private int x;
	private int y;

	public Position() {
	}

	public Position(Environment environment, int x, int y) {
		this.environment = environment;
		this.x = x;
		this.y = y;
	}

	public Environment getEnvironment() {
		return environment;
	}

	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public List<String> composePath(List<String> pathTarget) {

		if (pathTarget == null) {
			pathTarget = new LinkedList<String>();
		}

		// insert parents
		if (getEnvironment() != null) {
			getEnvironment().getPosition().composePath(pathTarget);
		}

		pathTarget.add("" + x + "/" + y);

		return pathTarget;
	}

	@Override
	public String toString() {
		Environment env = getEnvironment();

		String toString = env.getName() == null ? "Galaxy" : env.getName();
		toString = "[" + getX() + "/" + getY() + "] @ " + toString;

		return toString;
	}
}
