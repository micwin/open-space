package net.micwin.elysium.view.collective;

import java.text.NumberFormat;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import net.micwin.elysium.model.NaniteGroup;
import net.micwin.elysium.model.gates.Gate;
import net.micwin.elysium.view.BasePage;
import net.micwin.elysium.view.ElysiumWicketModel;
import net.micwin.elysium.view.EmptyLink;

import org.apache.wicket.Component;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
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

		final ElysiumWicketModel<NaniteGroup> groupModel = group != null ? new ElysiumWicketModel<NaniteGroup>(
						group) : null;

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
		return new Label("otherNanitesTable");
	}

}
