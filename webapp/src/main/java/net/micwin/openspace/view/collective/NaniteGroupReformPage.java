package net.micwin.openspace.view.collective;

import java.util.Iterator;

import net.micwin.openspace.entities.nanites.NaniteGroup;
import net.micwin.openspace.view.BasePage;
import net.micwin.openspace.view.ElysiumWicketModel;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

public class NaniteGroupReformPage extends BasePage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 79978466206769854L;

	public NaniteGroupReformPage() {
		super(false);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		ensureAvatarPresent(true);
		ensureSessionEntityPresent(NaniteGroup.class, "naniteGroup");
		addToBorder(composeTitle());

		addToContentBody(composeTable());
		addToContentBody(composeCancelLink());

	}

	private Component composeTitle() {
		return new Label("groupName", Model.of(getElysiumSession().getNamedEntity("naniteGroup").getName()));
	}

	private Component composeTable() {

		RefreshingView<NaniteGroup> table = new RefreshingView<NaniteGroup>("templateTable") {

			@Override
			protected Iterator<IModel<NaniteGroup>> getItemModels() {
				return ElysiumWicketModel.asModelList(
								getNanitesBPO().findFittingTemplates(
												(NaniteGroup) getElysiumSession().getNamedEntity("naniteGroup")))
								.iterator();
			}

			@Override
			protected void populateItem(Item<NaniteGroup> item) {

				NaniteGroup naniteGroup = item.getModelObject();
				item.add(composeSelectLink(naniteGroup));
				item.add(createLabel("groupId", naniteGroup.getId()));
				item.add(createLabel("groupName", naniteGroup.getName()));
				item.add(createLabel("groupLevel", naniteGroup.getGroupLevel()));
				item.add(createLabel("cslotsFree", getNanitesBPO().computeFreeSlots(naniteGroup)));
				item.add(createLabel("nslots", naniteGroup.getNaniteSlots()));
				item.add(createLabel("catapults", naniteGroup.getCatapults()));
				item.add(createLabel("flak", naniteGroup.getFlaks()));
				item.add(createLabel("satellites", naniteGroup.getSatellites()));
				item.add(createLabel("ambush", naniteGroup.getAmbushSquads()));
			}
		};
		return table;
	}

	protected Component composeSelectLink(NaniteGroup template) {
		return new Link<NaniteGroup>("select", ElysiumWicketModel.of(template)) {

			/**
			 * 
			 */
			private static final long serialVersionUID = -8808903973949375589L;

			@Override
			public void onClick() {
				getNanitesBPO().reform((NaniteGroup) getElysiumSession().getNamedEntity("naniteGroup"),
								getModelObject());
				setResponsePage(NaniteGroupShowPage.class);
			}
		};
	}

	private Component composeCancelLink() {
		return new Link("cancel") {
			@Override
			public void onClick() {
				setResponsePage(NaniteGroupShowPage.class);
			}

		};
	}

}
