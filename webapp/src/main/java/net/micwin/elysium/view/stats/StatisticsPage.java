package net.micwin.elysium.view.stats;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import net.micwin.elysium.dao.DaoManager;
import net.micwin.elysium.entities.characters.Avatar;
import net.micwin.elysium.entities.characters.User.Role;
import net.micwin.elysium.entities.characters.User.State;
import net.micwin.elysium.view.BasePage;
import net.micwin.elysium.view.ElysiumApplication;
import net.micwin.elysium.view.ElysiumWicketModel;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
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

		List<Avatar> avatars = new LinkedList<Avatar>(DaoManager.I.getAvatarDao().loadAll()) ;

		filterAdmins(avatars);
		Collections.sort(avatars, new Comparator<Avatar>() {

			@Override
			public int compare(Avatar o1, Avatar o2) {
				if (o1.getPoints() > o2.getPoints())
					return -1;
				if (o1.getPoints() < o2.getPoints()) {
					return 1;
				}
				return 0;
			}
		});

		// only show the first 10
		boolean notAdmin = getAvatar() == null || getAvatar().getUser().getRole() != Role.ADMIN;
		if (avatars.size() > 10 && notAdmin)
			avatars = avatars.subList(0, 10);

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
				String nameString = avatar.getName();
				if (avatar.getUser().getState() == State.PASSIVATED || avatar.getUser().getState() == State.PAUSED) {
					nameString = nameString + " (P)";
				}
				item.add(new Label("name", nameString));
				item.add(new Label("points", Model.of(avatar.getPoints())));
				item.add(new Label("groupsCount", Model.of(avatar.getNanites().size())));

				item.add(new Label("birthDate", Model.of(DateFormat.getDateInstance(DateFormat.SHORT, Locale.GERMANY)
								.format(avatar.getCreationDate()))));
				Date lastLoginDate = avatar.getUser().getLastLoginDate();
				item.add(new Label("lastLogin", Model.of(lastLoginDate != null ? DateFormat.getDateTimeInstance(
								DateFormat.SHORT, DateFormat.SHORT, Locale.GERMANY).format(lastLoginDate)
								: "<unbekannt>")));

				item.add(new Label("arenaWins", Model.of(NumberFormat.getIntegerInstance()
								.format(avatar.getArenaWins()))));
				item.add(getLeverageLink(avatar, 51));
				item.add(getLeverageLink(avatar, 101));
				item.add(getResurectLink(avatar));
				item.add(getResetLink(avatar));
				item.add(getTogglePassivateLink(avatar));
			}
		};

		return rankingTable;
	}

	protected Component getLeverageLink(Avatar avatar, final int targetLevel) {
		String id = "leverage" + targetLevel + "Link";

		if (!isAdmin() || avatar.getLevel() > targetLevel) {
			return createDummyLink(id, false, false);
		}

		final ElysiumWicketModel<Avatar> model = ElysiumWicketModel.of(avatar);
		Link leverageLink = new Link(id) {

			@Override
			public void onClick() {
				getAvatarBPO().leverage(model.getObject(), targetLevel);
				setResponsePage(StatisticsPage.class);

			}
		};
		return leverageLink;
	}

	protected Component getResurectLink(Avatar avatar) {
		if (!isAdmin() || getAvatarBPO().isAlive(avatar)) {

			return createDummyLink("resurrectLink", false, false);
		}

		final ElysiumWicketModel<Avatar> model = ElysiumWicketModel.of(avatar);

		Link resurrectLink = new Link("resurrectLink") {

			@Override
			public void onClick() {

				getAvatarBPO().resurrect(model.getObject());
				setResponsePage(StatisticsPage.class);
			}
		};

		return resurrectLink;
	}

	protected Component getResetLink(Avatar avatar) {

		if (!isAdmin()) {

			return createDummyLink("resetLink", false, false);
		}

		final ElysiumWicketModel<Avatar> model = ElysiumWicketModel.of(avatar);

		Link resurrectLink = new Link("resetLink") {

			@Override
			public void onClick() {

				getAvatarBPO().reset(model.getObject());
				setResponsePage(StatisticsPage.class);
			}
		};

		return resurrectLink;
	}

	protected Component getTogglePassivateLink(Avatar avatar) {

		if (!isAdmin()) {

			return createDummyLink("passivateLink", false, false);
		}

		final ElysiumWicketModel<Avatar> model = ElysiumWicketModel.of(avatar);

		Link togglePassivate = new Link("passivateLink") {

			@Override
			public void onClick() {

				getAvatarBPO().togglePassivate(model.getObject());
				setResponsePage(StatisticsPage.class);
			}
		};

		return togglePassivate;
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
