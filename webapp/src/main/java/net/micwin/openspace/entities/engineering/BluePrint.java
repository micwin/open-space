package net.micwin.openspace.entities.engineering;

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

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import net.micwin.openspace.entities.OpenSpaceEntity;
import net.micwin.openspace.entities.appliances.Utilization;
import net.micwin.openspace.entities.characters.Avatar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * a blueprint for a planetary base.
 * 
 * @author MicWin
 * 
 */
@Entity
public final class BluePrint extends OpenSpaceEntity {

	private static final Logger L = LoggerFactory.getLogger(BluePrint.class);

	@Enumerated(EnumType.STRING)
	private BluePrintType type;

	@OneToMany(cascade = CascadeType.ALL)
	private List<Utilization> utilizations;

	@OneToOne
	private Avatar owner;

	private int usageCounts;

	public BluePrint() {
	}

	public void setUtilizations(List<Utilization> utilizations) {
		this.utilizations = utilizations;
	}

	public List<Utilization> getUtilizations() {
		if (utilizations == null) {
			utilizations = new LinkedList<Utilization>();
		}
		return utilizations;
	}

	long getComplexity() {
		return type.getComplexityCalculator().calculateComplexity(this);
	}

	public void setOwner(Avatar owner) {
		this.owner = owner;
	}

	public Avatar getOwner() {
		return owner;
	}

	public void setUsageCounts(int usageCounts) {
		this.usageCounts = usageCounts;
	}

	public int getUsageCounts() {
		return usageCounts;
	}

	@Override
	public Class getBaseClass() {
		return BluePrint.class;
	}
}
