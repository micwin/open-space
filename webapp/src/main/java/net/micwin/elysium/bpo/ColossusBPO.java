package net.micwin.elysium.bpo;

import net.micwin.elysium.dao.DaoManager;
import net.micwin.elysium.entities.characters.Avatar;
import net.micwin.elysium.entities.characters.User.Role;

/**
 * Capsulates the business methodas of colossus handling.
 * 
 * @author MicWin
 * 
 */
public class ColossusBPO extends BaseBPO {

	public int countColossuses(Avatar controller) {
		return DaoManager.I.getColossusDao().findByController(controller).size();
	}

	/**
	 * Returns wether or not specified avatar may build at least one more
	 * colossus.
	 * 
	 * @param avatar
	 * @return
	 */
	public boolean canBuildColossus(Avatar avatar) {
		if (avatar.getUser().getRole() == Role.ADMIN) {
			return true;
		}

		if (avatar.getLevel() >= 100) {
			return true;
		}

		return false;
	}
}
