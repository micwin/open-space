package net.micwin.openspace.view.homepage;

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

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.micwin.openspace.bpo.BaseBPO;
import net.micwin.openspace.entities.appliances.Utilization;
import net.micwin.openspace.entities.characters.User;
import net.micwin.openspace.view.BasePageView;
import net.micwin.openspace.view.OpenSpaceWicketModel;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.model.IModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The landing page.
 * 
 * @author MicWin
 * 
 */
public class HomePage extends BasePageView {

	private static final Logger L = LoggerFactory.getLogger(HomePage.class);

	public HomePage() {
		super(true);
		ensureAvatarPresent(true);
		ensureStoryShown();

	}

	@Override
	protected void onInitialize() {
		super.onInitialize();

		addToContentBody(new Label("name", "" + getAvatar().getName()));
		addToContentBody(new Label("level", NumberFormat.getIntegerInstance(getLocale()).format(getAvatar().getLevel())));
		addToContentBody(new Label("frags", "" + getAvatar().getFragCount()));
		addToContentBody(new Label("deaths", NumberFormat.getIntegerInstance(getLocale()).format(getAvatar().getDeathCount())));

		addToContentBody(getTalents());

	}

	private Component getTalents() {

		Collection<Utilization> talents = getAvatar().getTalents();

		if (L.isDebugEnabled()) {
			L.debug("avatar " + getAvatar().getName() + " has  " + talents.size() + "talents");
			L.debug("" + talents);
		}

		final List<IModel> models = new ArrayList<IModel>();

		for (Utilization talent : talents) {
			models.add(new OpenSpaceWicketModel<Utilization>(talent));
		}

		Component talentsComponent = new RefreshingView<Utilization>("talents") {
			protected Iterator getItemModels() {
				return models.iterator();
			}

			protected void populateItem(Item<Utilization> item) {
				final IModel<Utilization> utilizationModel = item.getModel();
				final Utilization utilization = (Utilization) utilizationModel.getObject();
				item.add(new Label("label", utilization.getAppliance().getLabel()));
				item.add(composeToGoLabel(utilization));
				item.add(new Label("description", utilization.getAppliance().getDescription()));
				item.add(new Label("level", NumberFormat.getIntegerInstance().format(utilization.getLevel())));
			}

			protected Label composeToGoLabel(final Utilization utilization) {
				double percentage = 1.0 * utilization.getCount() / new BaseBPO().computeNextLevelUsages(utilization);
				String text = NumberFormat.getPercentInstance().format(percentage);
				return new Label("togo", text);
			}

		};
		return talentsComponent;
	}

	/**
	 * Determines wether or not the specified user may show this page.
	 * 
	 * @param user
	 * @return
	 */
	public static boolean userCanShow(User user) {
		return user != null;
	}
}
