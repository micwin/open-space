package net.micwin.openspace.view;

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
import net.micwin.openspace.dao.DaoManager;
import net.micwin.openspace.entities.characters.Avatar;
import net.micwin.openspace.entities.characters.User;

import org.apache.wicket.Session;
import org.apache.wicket.markup.html.panel.Panel;

public abstract class BasePanel extends Panel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1340374644079100324L;

	public BasePanel(String id) {
		super(id);
	}

	protected User getUser() {
		return getEylsiumSession().getUser();
	}

	protected OpenSpaceSession getEylsiumSession() {
		return (OpenSpaceSession) Session.get();
	}

	protected Avatar getAvatar() {
		return DaoManager.I.getAvatarDao().findByUser(getUser());
	}
	

	/**
	 * Returns a specific text key from the resource files (properties).
	 * 
	 * @param messageKey
	 * @return
	 */
	protected String getLocalizedMessage(String messageKey) {
		return getLocalizer().getString(messageKey, this);
	}
}
