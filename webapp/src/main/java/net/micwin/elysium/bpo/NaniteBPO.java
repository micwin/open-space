package net.micwin.elysium.bpo;

import net.micwin.elysium.model.NaniteGroup;
import net.micwin.elysium.model.appliances.Appliance;
import net.micwin.elysium.model.appliances.Utilization;

import org.slf4j.LoggerFactory;

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
/**
 * A bpo handling with nanite processes.
 * 
 * @author MicWin
 * 
 */
public class NaniteBPO extends BaseBPO {

	private static final org.slf4j.Logger L = LoggerFactory.getLogger(NaniteBPO.class);
	private static final int BASE_MAX_NANITES_GROUP_SIZE = 256;

	public void doubleCount(NaniteGroup nanitesGroup) {

		Utilization nanitemanagement = new AvatarBPO().getTalent(nanitesGroup.getController(),
						Appliance.NANITE_MANAGEMENT);

		long maxCount = (long) (BASE_MAX_NANITES_GROUP_SIZE * Math.pow(2, nanitemanagement.getLevel() - 1));

		long newCount = Math.min(nanitesGroup.getNaniteCount() * 2, maxCount);

		if (L.isDebugEnabled()) {
			L.debug("setting count from nanites group " + nanitesGroup.getId() + " from "
							+ nanitesGroup.getNaniteCount() + " to " + newCount);
		}
		nanitesGroup.setNaniteCount(newCount);
		getNanitesDao().save(nanitesGroup);
	}

}
