package net.micwin.elysium.view.collective;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import net.micwin.elysium.dao.DaoManager;
import net.micwin.elysium.entities.colossus.Colossus;
import net.micwin.elysium.entities.gates.Gate;
import net.micwin.elysium.view.BasePage;
import net.micwin.elysium.view.ElysiumWicketModel;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

public class ColossusListPage extends BasePage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 477693907566655698L;

	public ColossusListPage() {
		super(true);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		ensureAvatarPresent(true);
		addToContentBody(getColossusTable());
		addToContentBody(getBuildColossusLink());
	}

	private Component getColossusTable() {

		RefreshingView<Colossus> view = new RefreshingView<Colossus>("colosussesTable") {

			@Override
			protected Iterator<IModel<Colossus>> getItemModels() {
				return getModels().iterator();
			}

			@Override
			protected void populateItem(Item<Colossus> item) {

				Colossus colossus = item.getModelObject();

				item.add(new Label("id", Model.of(colossus.getId())));
				item.add(new Label("type", Model.of(colossus.getColossusType())));
				item.add(new Label("level", Model.of(colossus.getColossusLevel())));
				item.add(new Label("position", Model.of(colossus.getPosition().toString())));
				item.add(new Label("state", Model.of(colossus.getState())));

				Gate gate = DaoManager.I.getGatesDao().findByEnvironment(colossus.getPosition().getEnvironment())
								.iterator().next();
				item.add(new Label("gateAdress", Model.of(gate.getGateAdress())));
				item.add(createDummyLink("jump", false, false));

			}
		};

		return view;
	}

	protected Collection<IModel<Colossus>> getModels() {
		Collection<Colossus> entities = DaoManager.I.getColossusDao().findByController(getAvatar());
		Collection<IModel<Colossus>> models = new LinkedList<IModel<Colossus>>();

		for (Colossus entity : entities) {
			models.add(ElysiumWicketModel.of(entity));
		}
		return models;

	}

	private Link getBuildColossusLink() {
		Link link = new Link("create") {

			@Override
			public void onClick() {
				setResponsePage(ColossusCreatePage.class);
				return;
			}
		};

		return link;
	}
}
