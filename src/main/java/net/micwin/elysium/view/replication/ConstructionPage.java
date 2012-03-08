package net.micwin.elysium.view.replication;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.micwin.elysium.bpo.AvatarBPO;
import net.micwin.elysium.dao.DaoManager;
import net.micwin.elysium.model.characters.User;
import net.micwin.elysium.model.replication.BluePrint;
import net.micwin.elysium.model.replication.BuildPlan;
import net.micwin.elysium.view.BasePage;

import org.apache.commons.collections.ComparatorUtils;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;

public class ConstructionPage extends BasePage {

	public ConstructionPage() {
		super(true);
		ensureStoryShown();
		ensureAvatarPresent();
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();

		List<BluePrint> bluePrints = new AvatarBPO().getBluePrintsUsableBy(getAvatar());
		addToContentBody(composeBuildPlansList(bluePrints));
		addToContentBody(composeBluePrintTable(bluePrints));

	}

	private Component composeBuildPlansList(List<BluePrint> bluePrints) {
		List<BuildPlan> buildPlans = DaoManager.I.getBuildPlanDao().findByAvatar(getAvatar());

		StringBuilder builder = new StringBuilder();

		boolean firstAdded = false;
		for (BuildPlan buildPlan : buildPlans) {
			if (firstAdded) {
				builder.append(", ");
			}
			builder.append(buildPlan.getBlueprint().getName()).append("(");
			builder.append(buildPlan.getBuiltComponents().size()).append("/")
							.append(buildPlan.getBlueprint().getUtilizations().size());

			firstAdded = true;
		}
		return new Label("buildPlans", builder.toString());
	}

	private Component composeBluePrintTable(List<BluePrint> bluePrints) {
		String id = "bluePrintsTable";
		List<BluePrint> buildPlans = DaoManager.I.getBluePrintDao().findByController(getAvatar());

		Collections.sort(buildPlans, new Comparator<BluePrint>() {

			@Override
			public int compare(BluePrint o1, BluePrint o2) {

				return ComparatorUtils.NATURAL_COMPARATOR.compare(o2.getUsageCounts(), o1.getUsageCounts());
			}
		});

		StringBuilder builder = new StringBuilder();
		for (BluePrint bluePrint : bluePrints) {
			if (builder.length() > 0) {
				builder.append(", ");
			}
			builder.append(bluePrint.getName());
		}

		// TODO dummy entry
		return new Label(id, builder.toString());

	}

	public static boolean userCanShow(User user) {
		return user != null;
	}
}
