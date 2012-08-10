package net.micwin.elysium.view.construction;

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

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.micwin.elysium.bpo.AvatarBPO;
import net.micwin.elysium.dao.DaoManager;
import net.micwin.elysium.entities.characters.User;
import net.micwin.elysium.entities.engineering.BluePrint;
import net.micwin.elysium.entities.engineering.BuildPlan;
import net.micwin.elysium.view.BasePage;

import org.apache.commons.collections.ComparatorUtils;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;

public class ConstructionPage extends BasePage {

	public ConstructionPage() {
		super(true);
		ensureStoryShown();
		ensureAvatarPresent(true);
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
		return false;
	}
}
