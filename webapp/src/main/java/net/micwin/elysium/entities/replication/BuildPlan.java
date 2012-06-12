package net.micwin.elysium.entities.replication;

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

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import net.micwin.elysium.entities.ElysiumEntity;
import net.micwin.elysium.entities.characters.Avatar;

@Entity
public class BuildPlan extends ElysiumEntity {

	@OneToOne
	private BluePrint blueprint;

	@OneToOne
	private Component componentInBuild;

	@OneToMany
	private List<Component> builtComponents;

	public BuildPlan() {
	}

	public void setBlueprint(BluePrint blueprint) {
		this.blueprint = blueprint;
	}

	public BluePrint getBlueprint() {
		return blueprint;
	}

	public void setComponentInBuild(Component componentInBuild) {
		this.componentInBuild = componentInBuild;
	}

	public Component getComponentInBuild() {
		return componentInBuild;
	}

	public List<Component> getBuiltComponents() {
		return builtComponents;
	}

	public void setBuiltComponents(List<Component> builtComponents) {
		this.builtComponents = builtComponents;
	}

	@Override
	public Class<BuildPlan> getBaseClass() {
		return BuildPlan.class;
	};

}
