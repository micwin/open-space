package net.micwin.elysium.dao;

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

import java.util.Arrays;
import java.util.List;

import net.micwin.elysium.entities.appliances.Utilization;
import net.micwin.elysium.entities.characters.Avatar;
import net.micwin.elysium.entities.replication.BluePrint;

public class HibernateBluePrintDao extends ElysiumHibernateDaoSupport<BluePrint> implements IBluePrintDao {

	@Override
	public BluePrint create(Avatar owner, String nameKey, Utilization... utilizations) {
		BluePrint bluePrint = new BluePrint();
		bluePrint.setName(nameKey);
		bluePrint.setOwner(owner);
		bluePrint.setUtilizations(Arrays.asList(utilizations));
		super.insert(bluePrint, true);
		return bluePrint;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<BluePrint> findByController(Avatar avatar) {
		return lookupHql(" from BluePrint where owner.id=" + avatar.getId());
	}

	@Override
	public Class<BluePrint> getEntityClass() {
		return BluePrint.class;
	}

}
