package net.micwin.elysium.view.collective;

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

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import net.micwin.elysium.entities.characters.Avatar;
import net.micwin.elysium.entities.characters.User;
import net.micwin.elysium.entities.galaxy.Environment;
import net.micwin.elysium.entities.galaxy.Position;
import net.micwin.elysium.entities.gates.Gate;
import net.micwin.elysium.entities.nanites.NaniteGroup;
import net.micwin.elysium.entities.nanites.NaniteState;
import net.micwin.elysium.view.BasePage;
import net.micwin.elysium.view.ElysiumWicketModel;
import net.micwin.elysium.view.jumpGates.UsePlanetaryGatePage;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NaniteGroupListPage extends BasePage {

	private static final Logger L = LoggerFactory.getLogger(NaniteGroupListPage.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<IModel<NaniteGroup>> models;

	public NaniteGroupListPage() {
		super(true);

	}

	@Override
	protected void onInitialize() {
		super.onInitialize();

		ensureStoryShown();

		if (getAvatar().getNanites().size() == 1) {

			getElysiumSession().setNamedEntity(NaniteGroupShowPage.NE_NANITE_GROUP,
							getAvatar().getNanites().iterator().next());
			setResponsePage(NaniteGroupShowPage.class);
		}

		if (getSortProperty() == null) {
			getElysiumSession().setAttribute(ATTR_SORT_ASCENDING, Boolean.FALSE);
			getElysiumSession().setAttribute(ATTR_SORT_PROPERTY, "groupLevel");
		}

		addToContentBody(createSortPropertyLink("sortById", "ID", "id", true));
		addToContentBody(createSortPropertyLink("sortByLevel", "Stufe", "groupLevel", true));
		addToContentBody(createSortPropertyLink("sortByCount", "Anzahl", "naniteCount", true));
		addToContentBody(createSortPropertyLink("sortByState", "Status", "state", true));
		addToContentBody(createSortPropertyLink("sortByBattleCounter", "Kämpfe", "battleCounter", true));
		addToContentBody(getGroupsTable());
	}

	@Override
	protected void onBeforeRender() {
		super.onBeforeRender();
	}

	private Collection<IModel<NaniteGroup>> getModels() {

		L.debug("aquiring new models");
		if (models == null) {
			models = new ArrayList<IModel<NaniteGroup>>();
		} else {
			models.clear();
		}

		List<NaniteGroup> nanites = new LinkedList<NaniteGroup>(getAvatar().getNanites());

		sort(nanites);

		for (NaniteGroup naniteGroup : nanites) {
			models.add(ElysiumWicketModel.of(naniteGroup));
		}

		L.debug("having " + models.size() + " entries");

		return models;

	}

	private Component getGroupsTable() {

		Component groupsTable = new RefreshingView<NaniteGroup>("groupsTable") {
			/**
			 * 
			 */
			private static final long serialVersionUID = -8476174688323600468L;

			protected Iterator<IModel<NaniteGroup>> getItemModels() {
				return getModels().iterator();
			}

			@Override
			protected void onBeforeRender() {
				super.onBeforeRender();
			}

			protected void populateItem(Item<NaniteGroup> item) {
				final IModel naniteGroupModel = item.getModel();
				final NaniteGroup nanitesGroup = (NaniteGroup) naniteGroupModel.getObject();

				Link<NaniteGroup> groupIdLink = new Link<NaniteGroup>("groupId") {

					@Override
					public void onClick() {
						getElysiumSession().setNamedEntity("naniteGroup", (NaniteGroup) naniteGroupModel.getObject());
						setResponsePage(NaniteGroupShowPage.class);
					}
				};

				groupIdLink.add(new Label("groupId", Model.of(nanitesGroup.getId())));
				groupIdLink.setEnabled(nanitesGroup.getState() != NaniteState.PASSIVATED);

				item.add(groupIdLink);
				item.add(new Label("groupLevel", Model.of(nanitesGroup.getGroupLevel())));

				Position position = nanitesGroup.getPosition();
				Gate gate = getGateBPO().getGateAt(position.getEnvironment());
				String gateCode = gate == null ? "---" : gate.getGateAdress();

				item.add(new Label("gateAdress", gateCode));

				Label label = new Label("groupPosition", position.getEnvironment().toString());
				item.add(label);
				Model<String> countModel = new Model<String>(NumberFormat.getIntegerInstance().format(
								nanitesGroup.getNaniteCount()));
				item.add(new Label("groupCount", countModel));
				String stateString = nanitesGroup.getState().toString();

				long maxStructurePoints = getNanitesBPO().computeStructurePoints(nanitesGroup);
				if (nanitesGroup.getStructurePoints() < maxStructurePoints) {
					String sp = NumberFormat.getPercentInstance(Locale.GERMANY).format(
									1.0 * nanitesGroup.getStructurePoints() / maxStructurePoints);
					stateString += " " + sp;
				}

				if (nanitesGroup.getStateEndGT() != null && nanitesGroup.getStateEndGT().after(new Date())) {
					stateString = stateString
									+ " (bis "
									+ DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(
													nanitesGroup.getStateEndGT()) + ")";
				}

				item.add(new Label("groupState", new Model(stateString)));
				item.add(new Label("battleCount", new Model(NumberFormat.getIntegerInstance().format(
								nanitesGroup.getBattleCounter()))));

				item.add(getDoubleCountLink(naniteGroupModel, countModel));
				item.add(getGateLink(naniteGroupModel));

				item.add(getSplitLink(naniteGroupModel));
				item.add(getKillLink(naniteGroupModel));
				item.add(getEntrenchLink(naniteGroupModel));
				item.add(getExitLink(naniteGroupModel));
				item.add(getCloneLink(naniteGroupModel));

			}

			private Component getEntrenchLink(final IModel<NaniteGroup> naniteGroupModel) {
				Link<NaniteGroup> entrenchLink = new Link<NaniteGroup>("entrench", naniteGroupModel) {

					/**
					 * 
					 */
					private static final long serialVersionUID = -7730841008038281114L;

					@Override
					public void onClick() {
						getNanitesBPO().entrench(getModelObject());
					}

				};

				entrenchLink.setVisible(getNanitesBPO().canEntrench(naniteGroupModel.getObject()));
				return entrenchLink;
			}

			protected Link getCloneLink(final IModel<NaniteGroup> groupModel) {
				Link link = new Link("clone") {

					/**
					 * 
					 */
					private static final long serialVersionUID = -213962075156991317L;

					@Override
					public void onClick() {
						getNanitesBPO().split(groupModel.getObject(), false, true);
						setResponsePage(NaniteGroupListPage.class);
					}
				};

				link.setVisible(getNanitesBPO().canSplit(groupModel.getObject(), false, true));
				return link;
			}

			protected Link getSplitLink(final IModel<NaniteGroup> naniteGroupModel) {
				Link link = new Link("split") {

					/**
					 * 
					 */
					private static final long serialVersionUID = -213962075156991317L;

					@Override
					public void onClick() {
						getNanitesBPO().split(naniteGroupModel.getObject(), true, false);
						setResponsePage(NaniteGroupListPage.class);
					}
				};

				link.setVisible(getNanitesBPO().canSplit(naniteGroupModel.getObject(), true, false));
				return link;
			}

			protected Link getKillLink(final IModel<NaniteGroup> naniteGroupModel) {
				Link link = new Link("kill") {

					/**
					 * 
					 */
					private static final long serialVersionUID = 7583594232053856025L;

					@Override
					public void onClick() {
						getNanitesBPO().kill(naniteGroupModel.getObject());
						setResponsePage(NaniteGroupListPage.class);
					}
				};

				link.setVisible(naniteGroupModel.getObject().getState() != NaniteState.PASSIVATED);
				return link;
			}

			private Component getGateLink(final IModel<NaniteGroup> naniteGroupModel) {

				final NaniteGroup naniteGroup = naniteGroupModel.getObject();
				Position position = naniteGroup.getPosition();
				Environment environment = position.getEnvironment();
				Gate gate = getGateBPO().getGateAt(environment);

				Link<Gate> gateLink = new Link<Gate>("jumpGate") {
					public void onClick() {
						getElysiumSession().setNamedEntity("naniteGroup", naniteGroupModel.getObject());
						setResponsePage(UsePlanetaryGatePage.class);
					};
				};
				gateLink.setVisible(gate != null && getNanitesBPO().canJumpGate(naniteGroup));

				return gateLink;
			}

			private Component getDoubleCountLink(final IModel<NaniteGroup> nanitesGroupModel,
							final IModel<String> labelModel) {
				Link link = new Link("doubleCount") {

					@Override
					public void onClick() {
						getNanitesBPO().doubleCount(nanitesGroupModel.getObject());
						setResponsePage(NaniteGroupListPage.class);
					}
				};
				Avatar controller = nanitesGroupModel.getObject().getController();

				link.setVisible(getNanitesBPO().canRaiseNanitesCount(nanitesGroupModel.getObject()));
				return link;
			}
		};

		return groupsTable;
	}

	/**
	 * 
	 * @param user
	 * @return
	 */
	public static boolean userCanShow(User user) {
		return user != null;
	}

	private Component getExitLink(IModel<NaniteGroup> model) {
		Link exitLink = new Link<NaniteGroup>("exit", model) {

			@Override
			public void onClick() {
				getNanitesBPO().exit(getModelObject());
				setResponsePage(NaniteGroupListPage.class);
			}
		};

		exitLink.setVisible(getNanitesBPO().canExit(model.getObject()));
		return exitLink;
	}
}
