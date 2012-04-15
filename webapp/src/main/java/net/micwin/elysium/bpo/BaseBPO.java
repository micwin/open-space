package net.micwin.elysium.bpo;

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

import net.micwin.elysium.dao.DaoManager;
import net.micwin.elysium.dao.IAvatarDao;
import net.micwin.elysium.dao.IBluePrintDao;
import net.micwin.elysium.dao.IGalaxyDao;
import net.micwin.elysium.dao.IGatesDao;
import net.micwin.elysium.dao.INanitesDao;
import net.micwin.elysium.dao.ISysParamDao;
import net.micwin.elysium.dao.ITalentsDao;
import net.micwin.elysium.dao.IUserDao;
import net.micwin.elysium.model.GalaxyTimer;
import net.micwin.elysium.view.ElysiumApplication;

import org.apache.wicket.Application;

/**
 * some base utilities for BPOs.
 * 
 * @author MicWin
 * 
 */
public class BaseBPO {

	private static DaoManager daoManager;

	protected IUserDao getUserDao() {
		return getDaoManager().getUserDao();
	}

	protected IAvatarDao getAvatarDao() {
		return getDaoManager().getAvatarDao();
	}

	public ISysParamDao getSysParamDao() {
		return getDaoManager().getSysParamDao();
	}

	public GalaxyTimer getGalaxyTimer() {
		return ((ElysiumApplication) Application.get()).getGalaxyTimer();
	}

	public IGalaxyDao getGalaxyDao() {
		return getDaoManager().getGalaxyDao();
	}
	
	public ITalentsDao getTalentsDao () {
		return getDaoManager().getTalentsDao() ; 
	}
	

	public INanitesDao getNanitesDao() {
		return getDaoManager().getNanitesDao();
	}

	public IBluePrintDao getBluePrintDao() {
		return getDaoManager().getBluePrintDao();
	}

	/**
	 * DONT CALL THIS METHOD! Its a hack and surely will be replaced by a more
	 * elegant construct.
	 * 
	 * @param newDaoManager
	 */
	@Deprecated
	public void setDaoManager(DaoManager newDaoManager) {
		daoManager = newDaoManager;
	}

	protected DaoManager getDaoManager() {
		return daoManager;
	}
	

	protected IGatesDao getGatesDao() {
		return getDaoManager().getGatesDao();
	}


}