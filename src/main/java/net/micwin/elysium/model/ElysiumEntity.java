package net.micwin.elysium.model;

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

 open-space ist Freie Software: Sie können es unter den Bedingungen
 der GNU Affero Public License, wie von der Free Software Foundation,
 Version 3 der Lizenz oder (nach Ihrer Option) jeder späteren
 veröffentlichten Version, weiterverbreiten und/oder modifizieren.

 open-space wird in der Hoffnung, dass es nützlich sein wird, aber
 OHNE JEDE GEWÄHELEISTUNG, bereitgestellt; sogar ohne die implizite
 Gewährleistung der MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK.
 Siehe die GNU Affero Public License für weitere Details.

 Sie sollten eine Kopie der GNU Affero Public License zusammen mit diesem
 Programm erhalten haben. Wenn nicht, siehe http://www.gnu.org/licenses. 

 */

import java.io.Serializable;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

@MappedSuperclass
public abstract class ElysiumEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Transient
	private final Class<? extends ElysiumEntity> baseClass;

	/**
	 * Call this in your default constructor.
	 * 
	 * @param baseClass
	 *            the base class of this entity. Use explicit types, dont use
	 *            getClass() since this may return any enhanced class code.
	 */
	protected ElysiumEntity(Class<? extends ElysiumEntity> baseClass) {
		this.baseClass = baseClass;
	}

	public void setId(Serializable id) {
		this.id = (Long) id;
	}

	public Serializable getId() {
		return id;
	}

	/**
	 * the base class of this entity. Use explicit types, dont use getClass()
	 * since this may return any enhanced class code.
	 * 
	 * @return
	 */
	public Class getBaseClass() {
		return baseClass;
	}
}
