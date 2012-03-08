package net.micwin.elysium.dao;

import java.util.List;

import net.micwin.elysium.model.appliances.Utilization;
import net.micwin.elysium.model.characters.Avatar;
import net.micwin.elysium.model.replication.BluePrint;

/**
 * The dao to insert, locate, update and delete blue prints.
 * 
 * @author MicWin
 * 
 */
public interface IBluePrintDao extends IElysiumEntityDao<BluePrint>{

	/**
	 * creates and persists a new blueprint.
	 * 
	 * @param owner
	 * @param nameKey
	 * @param utilizations
	 * @return
	 */
	BluePrint create(Avatar owner, String nameKey, Utilization... utilizations);

	/**
	 * Locates all blueprints under control by passed avatar.
	 * 
	 * @param avatar
	 * @return
	 */
	List<BluePrint> findByController(Avatar avatar);

}
