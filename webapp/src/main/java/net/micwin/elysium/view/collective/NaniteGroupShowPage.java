package net.micwin.elysium.view.collective;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.micwin.elysium.entities.NaniteGroup;
import net.micwin.elysium.entities.galaxy.Environment;
import net.micwin.elysium.entities.galaxy.Position;
import net.micwin.elysium.entities.gates.Gate;
import net.micwin.elysium.view.BasePage;
import net.micwin.elysium.view.ElysiumWicketModel;
import net.micwin.elysium.view.EmptyLink;
import net.micwin.elysium.view.jumpGates.UsePlanetaryGatePage;

import org.apache.wicket.Component;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.string.StringValue;

public class NaniteGroupShowPage extends BasePage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1446425437846982751L;

	public NaniteGroupShowPage() {
		super(true);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		ensureAvatarPresent();
		ensureLoggedIn();
		ensureStoryShown();

		NaniteGroup group = getElysiumSession().getNamedEntity("naniteGroup");

		addToContentBody(new Label("groupPosition", "" + group.getPosition().getEnvironment()));

		String gateCode = getGateBPO().getGateAt(group.getPosition().getEnvironment()).getGateAdress();

		addToContentBody(new Label("groupGate", "" + gateCode));
		addToContentBody(new Label("groupCount", NumberFormat.getIntegerInstance().format(group.getNaniteCount())));
		addToContentBody(new Label("groupState", "" + group.getState()));

		addToContentBody(composeLocalJumpItems(group));
		addToContentBody(getOtherNanitesTable(group));

	}

	private Collection<Component> composeLocalJumpItems(NaniteGroup group) {

		Collection<Component> result = new LinkedList<Component>();
		List<Gate> scannedGates = getScannerBPO().scanForGates(group);

		final ElysiumWicketModel<NaniteGroup> groupModel = group != null ? new ElysiumWicketModel<NaniteGroup>(group)
						: null;

		for (int index = 0; index < 3; index++) {
			String linkWickedId = "jumpToP" + index;

			Gate gate = scannedGates.size() > index ? scannedGates.get(index) : null;

			if (gate == null) {

				Link link = new EmptyLink(linkWickedId);

				link.add(new Label("label", "---"));

				result.add(link);
				continue;
			}

			if (gate.getPosition().getEnvironment().equals(group.getPosition().getEnvironment())) {

				Link link = new EmptyLink(linkWickedId);

				link.add(new Label("label", gate.getGateAdress()));
				result.add(link);
				continue;
			}

			final ElysiumWicketModel<Gate> gateModel = new ElysiumWicketModel<Gate>(gate);

			Link<String> jumpLink = new Link<String>(linkWickedId, Model.of(gate.getGateAdress())) {

				/**
				 * 
				 */
				private static final long serialVersionUID = -2578829572024287457L;

				@Override
				public void onClick() {
					getNanitesBPO().gateTravel(groupModel.getEntity(), gateModel.getEntity().getGateAdress());
					setResponsePage(NaniteGroupShowPage.class);
				}
			};

			jumpLink.add(new Label("label", gate.getGateAdress()));

			result.add(jumpLink);
		}

		return result;

	}

	private Component getOtherNanitesTable(NaniteGroup scanningGroup) {

		Iterator<NaniteGroup> nanites = getScannerBPO().scanForOtherNaniteGroups(scanningGroup).iterator();

		final List<IModel> models = new ArrayList<IModel>();
		while (nanites.hasNext()) {
			models.add(new ElysiumWicketModel<NaniteGroup>(nanites.next()));
		}

		Component otherNanitesTable = new RefreshingView<NaniteGroup>("otherNanitesTable") {
			protected Iterator getItemModels() {

				return models.iterator();
			}

			protected void populateItem(Item item) {
				final IModel naniteGroupModel = item.getModel();
				final NaniteGroup nanitesGroup = (NaniteGroup) naniteGroupModel.getObject();

				Position position = nanitesGroup.getPosition();
				Gate gate = getGateBPO().getGateAt(position.getEnvironment());
				String gateCode = gate.getGateAdress();

				item.add(new Label("owner", new Model(nanitesGroup.getController().getName())));
				item.add(new Label("strength", new Model(nanitesGroup.getNaniteCount())));
				item.add(new Label("commands", ""));

			}
		};

		return otherNanitesTable;
	}

}
