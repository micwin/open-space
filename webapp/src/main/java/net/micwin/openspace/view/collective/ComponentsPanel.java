package net.micwin.openspace.view.collective;

import net.micwin.openspace.bpo.NaniteBPO;
import net.micwin.openspace.dao.DaoManager;
import net.micwin.openspace.entities.nanites.NaniteGroup;
import net.micwin.openspace.view.BasePanel;

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
		add(new Label("naniteSlots", "" + group.getNaniteSlots()));
		add(new Label("satellites", "" + group.getSatellites()));
		add(new Label("flaks", "" + group.getFlaks()));
		add(new Label("ambushSquads", "" + group.getAmbushSquads()));

		
		
		add(composeRaiseCatapultsLink());
		add(composeRaiseNaniteSlotLink());
		add(composeRaiseSatellitesLink());
		add(composeRaiseFlaksLink());
		add(composeRaiseAmbushSquadsLink());

		
		
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
				setResponsePage(getPage().getClass());
			}
		};
		raiseCatapultsLink.setVisible(new NaniteBPO().canRaiseComponents(groupModel.getObject()));
		return raiseCatapultsLink;
	}

	private Component composeRaiseNaniteSlotLink() {

		Link raiseCatapultsLink = new Link("raiseNaniteSlots") {

			@Override
			public void onClick() {
				NaniteBPO bpo = new NaniteBPO();
				NaniteGroup group = groupModel.getObject();
				if (!new NaniteBPO().canRaiseComponents(group)) {
					return;
				}

				group.setNaniteSlots(group.getNaniteSlots() + 1);
				DaoManager.I.getNanitesDao().update(group);
				setResponsePage(getPage().getClass());
			}
		};
		raiseCatapultsLink.setVisible(new NaniteBPO().canRaiseComponents(groupModel.getObject()));
		return raiseCatapultsLink;
	}

	private Component composeRaiseSatellitesLink() {

		@SuppressWarnings({ "rawtypes", "serial" })
		Link raiseSatellitesLink = new Link("raiseSatellites") {

			@Override
			public void onClick() {
				NaniteBPO bpo = new NaniteBPO();
				NaniteGroup group = groupModel.getObject();
				if (!new NaniteBPO().canRaiseComponents(group)) {
					return;
				}

				group.setSatellites(group.getSatellites() + 1);
				DaoManager.I.getNanitesDao().update(group);
				setResponsePage(getPage().getClass());
			}
		};
		raiseSatellitesLink.setVisible(new NaniteBPO().canRaiseComponents(groupModel.getObject()));
		return raiseSatellitesLink;
	}

	private Component composeRaiseFlaksLink() {

		@SuppressWarnings({ "rawtypes", "serial" })
		Link raiseFlaksLink = new Link("raiseFlaks") {

			@Override
			public void onClick() {
				new NaniteBPO().raiseFlaks(groupModel.getObject());
				setResponsePage(getPage().getClass());
			}
		};
		raiseFlaksLink.setVisible(new NaniteBPO().canRaiseComponents(groupModel.getObject()));
		return raiseFlaksLink;
	}

	
	private Component composeRaiseAmbushSquadsLink() {

		@SuppressWarnings({ "rawtypes", "serial" })
		Link raiseFlaksLink = new Link("raiseAmbushSquads") {

			@Override
			public void onClick() {
				new NaniteBPO().raiseAmbushSquads(groupModel.getObject());
				setResponsePage(getPage().getClass());
			}
		};
		raiseFlaksLink.setVisible(new NaniteBPO().canRaiseComponents(groupModel.getObject()));
		return raiseFlaksLink;
	}
}
