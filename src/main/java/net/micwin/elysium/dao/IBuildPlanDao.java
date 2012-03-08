package net.micwin.elysium.dao;

import java.util.List;

import net.micwin.elysium.model.characters.Avatar;
import net.micwin.elysium.model.replication.BuildPlan;

/**
 * a dao for handling with buildPlans.
 * 
 * @author MicWin
 * 
 */
public interface IBuildPlanDao extends IElysiumEntityDao<BuildPlan> {

	/**
	 * stores / saves / updates a (new) build plan entity.
	 * 
	 * @param buildPlan
	 */
	void save(BuildPlan buildPlan);

	/**
	 * Returns all build plans of one avatar.
	 * 
	 * @param avatar
	 */
	List <BuildPlan> findByAvatar(Avatar avatar);

}
