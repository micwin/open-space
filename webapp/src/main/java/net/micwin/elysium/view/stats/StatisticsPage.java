package net.micwin.elysium.view.stats;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.micwin.elysium.dao.DaoManager;
import net.micwin.elysium.entities.characters.Avatar;
import net.micwin.elysium.entities.characters.User.Role;
import net.micwin.elysium.view.BasePage;
import net.micwin.elysium.view.ElysiumApplication;
import net.micwin.elysium.view.ElysiumWicketModel;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

public class StatisticsPage extends BasePage {

	public StatisticsPage() {
		super(false);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		addToContentBody(createUserCountLabel());
		addToContentBody(createStartupTimeLabel());
		addToContentBody(new Label("gateCount", Model.of(DaoManager.I.getGatesDao().countEntries())));

		addToContentBody(createRankingTable());
	}

	private Component createStartupTimeLabel() {
		ElysiumApplication app = (ElysiumApplication) getElysiumSession().getApplication();
		return new Label("startupTime", Model.of(app.getStartupTime().toString()));
	}

	private Component createUserCountLabel() {
		return new Label("userCount", Model.of(DaoManager.I.getUserDao().countEntries()));
	}

	private Component createRankingTable() {

		List<Avatar> avatars = new LinkedList<Avatar>();
		DaoManager.I.getAvatarDao().loadAll(avatars);

		filterAdmins(avatars);
		Collections.sort(avatars, new Comparator<Avatar>() {

			@Override
			public int compare(Avatar o1, Avatar o2) {
				if (o1.getLevel() > o2.getLevel())
					return -1;
				if (o1.getLevel() < o2.getLevel()) {
					return 1;
				}
				return 0;
			}
		});

		final List<IModel<Avatar>> modelList = new LinkedList<IModel<Avatar>>();

		for (Avatar avatar : avatars) {
			modelList.add(new ElysiumWicketModel<Avatar>(avatar));
		}

		RefreshingView<Avatar> rankingTable = new RefreshingView<Avatar>("rankingTable") {

			@Override
			protected Iterator<IModel<Avatar>> getItemModels() {
				return modelList.iterator();
			}

			@Override
			protected void populateItem(Item<Avatar> item) {
				Avatar avatar = item.getModelObject();
				item.add(new Label("name", avatar.getName()));
				item.add(new Label("level", Model.of(avatar.getLevel())));
				item.add(new Label("groupsCount", Model.of(avatar.getNanites().size())));
				item.add(new Label("totalNanitesCount", Model.of(getNanitesBPO().countNanites(avatar))));

				item.add(new Label("birthDate", Model.of(avatar.getCreationDate().toString())));
				Date lastLoginDate = avatar.getUser().getLastLoginDate();
				item.add(new Label("lastLogin", Model.of(lastLoginDate != null ? lastLoginDate.toString()
								: "<unbekannt>")));

			}
		};

		return rankingTable;
	}

	private void filterAdmins(List<Avatar> avatars) {

		List<Avatar> admins = new LinkedList<Avatar>();

		for (Avatar avatar : avatars) {
			if (avatar.getUser().getRole() == Role.ADMIN) {
				admins.add(avatar);
			}
		}

		avatars.removeAll(admins);
	}
}