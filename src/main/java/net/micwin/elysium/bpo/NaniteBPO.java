package net.micwin.elysium.bpo;

import java.util.LinkedList;

import net.micwin.elysium.model.characters.Avatar;
import net.micwin.elysium.model.replication.BluePrint;
import net.micwin.elysium.model.replication.BuildPlan;
import net.micwin.elysium.model.replication.Component;

import org.slf4j.LoggerFactory;

/**
 * A bpo handling with nanite processes.
 * 
 * @author MicWin
 * 
 */
public class NaniteBPO extends BaseBPO {

	private static final org.slf4j.Logger L = LoggerFactory.getLogger(NaniteBPO.class);

	/**
	 * Starts building the given blue print.
	 * 
	 * @param bluePrint
	 * @param avatar 
	 */
	public void startBuilding(BluePrint bluePrint, Avatar avatar) {
		BuildPlan buildPlan = new BuildPlan();
		buildPlan.setBlueprint(bluePrint);
		buildPlan.setController (avatar) ; 
		buildPlan.setBuiltComponents(new LinkedList<Component> ()) ; 
		getDaoManager().getBuildPlanDao().save(buildPlan);
		if (L.isDebugEnabled()) {
			L.debug("created build plan for bluePrint '" + bluePrint.getName() + "'");
		}

	}
}
