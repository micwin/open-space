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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.micwin.elysium.bpo.GateBPO;
import net.micwin.elysium.bpo.NaniteBPO;
import net.micwin.elysium.dao.DaoManager;
import net.micwin.elysium.model.NaniteGroup;
import net.micwin.elysium.model.characters.User;
import net.micwin.elysium.model.galaxy.Environment;
import net.micwin.elysium.model.galaxy.Position;
import net.micwin.elysium.model.gates.Gate;
import net.micwin.elysium.view.BasePage;
import net.micwin.elysium.view.ElysiumLoadableDetachableModel;
import net.micwin.elysium.view.jumpGates.UsePlanetaryGatePage;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

public class NaniteGroupListPage extends BasePage {

	@SpringBean
	DaoManager daoManager;
	private Component groupsTable;

	private NaniteBPO nanitesBPO = new NaniteBPO();

	GateBPO gateBPO = new GateBPO();

	public NaniteGroupListPage() {
		super(true);
		ensureStoryShown();
		ensureAvatarPresent();
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		nanitesBPO = new NaniteBPO();

		addToContentBody(getGroupsTable());
	}

	@Override
	protected void onBeforeRender() {
		super.onBeforeRender();
	}

	private Component getGroupsTable() {

		if (groupsTable == null) {

			final Iterator<NaniteGroup> nanites = getAvatar().getNanites().iterator();

			groupsTable = new RefreshingView<NaniteGroup>("groupsTable") {
				protected Iterator getItemModels() {
					List<IModel> models = new ArrayList<IModel>();
					while (nanites.hasNext()) {
						models.add(new ElysiumLoadableDetachableModel<NaniteGroup>(nanites.next()));
					}
					return models.iterator();
				}

				protected void populateItem(Item item) {
					IModel naniteGroupModel = item.getModel();
					final NaniteGroup nanitesGroup = (NaniteGroup) naniteGroupModel.getObject();

					Position position = nanitesGroup.getPosition();
					Gate gate = gateBPO.getGateAt(position.getEnvironment());
					String gateCode = gate.getGateAdress();

					Label label = new Label("label", new Model(gateCode));
					Link link = new Link("groupPosition") {

						@Override
						public void onClick() {
							PageParameters params = new PageParameters(getPageParameters());
							params.set("groupId", nanitesGroup.getId());
							setResponsePage(NaniteGroupShowPage.class, params);
						}
					};
					link.add(label);
					item.add(link);
					Model<Long> countModel = new Model<Long>(nanitesGroup.getNaniteCount());
					item.add(new Label("groupCount", countModel));
					item.add(new Label("groupState", new Model(nanitesGroup.getState())));
					item.add(getDoubleCountLink(naniteGroupModel, countModel));
					item.add(getGateLink(naniteGroupModel));
				}

				private Component getGateLink(IModel<NaniteGroup> naniteGroupModel) {

					NaniteGroup naniteGroup = naniteGroupModel.getObject();
					Position position = naniteGroup.getPosition();
					Environment environment = position.getEnvironment();
					Gate gate = gateBPO.getGateAt(environment);

					BookmarkablePageLink<Gate> gateLink = new BookmarkablePageLink<Gate>("jumpGate",
									UsePlanetaryGatePage.class);
					gateLink.setVisible(gate != null);

					return gateLink;
				}

				private Component getDoubleCountLink(final IModel<NaniteGroup> nanitesGroupModel,
								final IModel<Long> countModel) {
					Link link = new Link("doubleCount") {

						@Override
						public void onClick() {
							nanitesBPO.doubleCount(nanitesGroupModel.getObject());
							setResponsePage(NaniteGroupListPage.class);
						}
					};

					return link;
				}

			};

		}
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
}
