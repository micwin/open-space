package net.micwin.elysium.entities;

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
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import net.micwin.elysium.entities.characters.Avatar;

@MappedSuperclass
public abstract class ElysiumEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	private String name;

	@ManyToOne(optional = true, fetch = FetchType.EAGER)
	private Avatar controller;

	/**
	 * Call this in your default constructor.
	 * 
	 * @param baseClass
	 *            the base class of this entity. Use explicit types, dont use
	 *            getClass() since this may return any enhanced class code.
	 */
	protected ElysiumEntity() {
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	/**
	 * the base class of this entity. Use explicit types, dont use getClass()
	 * since this may return any enhanced class code.
	 * 
	 * @return
	 */
	public abstract Class getBaseClass();

	public void setController(Avatar controller) {
		this.controller = controller;
	}

	public Avatar getController() {
		return controller;
	}

	@Override
	public int hashCode() {
		return (int) (getBaseClass().hashCode() + getId());
	}

	@Override
	public boolean equals(Object obj) {

		if (obj == this)
			return true;
		if (obj == null)
			return false;

		try {

			ElysiumEntity other = (ElysiumEntity) obj;
			return (other.id == id) && other.getBaseClass().equals(getBaseClass());
		} catch (ClassCastException cce) {

			return false;
		}
	}

	@Override
	public String toString() {
		return getBaseClass().getSimpleName() + " id=" + id + " hashCode=" + hashCode();
	}

	public final void setName(String name) {
		this.name = name;
	}

	public final String getName() {
		if (name == null) {
			name = getBaseClass().getSimpleName() + "#" + getId();
		}
		return name;
	}

}
