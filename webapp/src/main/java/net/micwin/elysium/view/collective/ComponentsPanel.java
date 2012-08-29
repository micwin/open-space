package net.micwin.elysium.view.collective;

import net.micwin.elysium.bpo.NaniteBPO;
import net.micwin.elysium.dao.DaoManager;
import net.micwin.elysium.entities.nanites.NaniteGroup;
import net.micwin.elysium.view.BasePanel;

import org.apache.wicket.Component;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;

public class ComponentsPanel extends BasePanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -167282604963308316L;
	private final IModel<NaniteGroup> groupModel;

	public ComponentsPanel(String id, IModel<NaniteGroup> groupModel) {
		super(id);
		this.groupModel = groupModel;
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		NaniteGroup group = groupModel.getObject();
		int freeSlots = new NaniteBPO().computeFreeSlots(group);
		add(new Label("freeSlots", "" + freeSlots));
		add(new Label("catapults", "" + group.getCatapults()));
		add(composeRaiseCatapultsLink());

	}

	private Component composeRaiseCatapultsLink() {

		Link raiseCatapultsLink = new Link("raiseCatapults") {

			@Override
			public void onClick() {
				NaniteBPO bpo = new NaniteBPO();
				NaniteGroup group = groupModel.getObject();
				if (!new NaniteBPO().canRaiseComponents(group)) {

					return;
				}

				group.setCatapults(group.getCatapults() + 1);
				DaoManager.I.getNanitesDao().update(group);
				throw new RestartResponseException(getPage());
			}
		};
		raiseCatapultsLink.setVisible(new NaniteBPO().canRaiseComponents(groupModel.getObject()));
		return raiseCatapultsLink;
	}
}
