package net.micwin.elysium.view.collective;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import net.micwin.elysium.bpo.GateBPO;
import net.micwin.elysium.bpo.NaniteBPO;
import net.micwin.elysium.entities.characters.Avatar;
import net.micwin.elysium.entities.galaxy.Environment;
import net.micwin.elysium.entities.galaxy.Position;
import net.micwin.elysium.entities.gates.Gate;
import net.micwin.elysium.entities.nanites.NaniteGroup;
import net.micwin.elysium.entities.nanites.NaniteState;
import net.micwin.elysium.view.BasePage;
import net.micwin.elysium.view.ElysiumWicketModel;
import net.micwin.elysium.view.jumpGates.UsePlanetaryGatePage;
import net.micwin.elysium.view.messages.MessageCreatePage;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;

public class NaniteGroupShowPage extends BasePage {

	static final String NE_NANITE_GROUP = "naniteGroup";
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

		ensureAvatarPresent(true);
		ensureLoggedIn();
		ensureStoryShown();

		NaniteGroup group = getElysiumSession().getNamedEntity(NE_NANITE_GROUP);

		if (group.getState() == NaniteState.PASSIVATED) {
			getElysiumSession().setNamedEntity(NE_NANITE_GROUP, null);
			setResponsePage(NaniteGroupListPage.class);
		}

		final ElysiumWicketModel<NaniteGroup> groupModel = new ElysiumWicketModel<NaniteGroup>(group);
		addToContentBody(new Label("groupId", "" + group.getId()));

		addToContentBody(new Label("groupLevel", Model.of(group.getGroupLevel())));

		addToContentBody(new Label("groupPosition", "" + group.getPosition().getEnvironment()));

		Gate gate = getGateBPO().getGateAt(group.getPosition().getEnvironment());
		String gateCode = gate == null ? "---" : gate.getGateAdress();

		addToContentBody(new Label("groupGate", "" + gateCode));
		addToContentBody(new Label("groupCount", NumberFormat.getIntegerInstance().format(group.getNaniteCount())));
		addToContentBody(new Label("signatureStrength", ""
						+ new DecimalFormat("0.0####", DecimalFormatSymbols.getInstance()).format(getScannerBPO()
										.computeSignatureStrength(group))));

		String state = group.getState().toString();

		long maxStructurePoints = getNanitesBPO().computeStructurePoints(group);
		if (group.getStructurePoints() < maxStructurePoints) {
			String sp = NumberFormat.getPercentInstance(Locale.GERMANY).format(
							1.0 * group.getStructurePoints() / maxStructurePoints);
			state += " " + sp;
		}
		addToContentBody(new Label("groupState", "" + state));
		addToContentBody(new Label("envState", getEnvStateString(group)));

		addToContentBody(getDoubleCountLink(groupModel));

		addToContentBody(composeJumpLink(groupModel));

		addToContentBody(composeQuickJumpItems(group));
		addToContentBody(getOtherNanitesTable(group));
		addToContentBody(getSplitLink(groupModel));
		addToContentBody(composeTitleText(group));
		addToContentBody(composeUpgradeLink(groupModel));
		addToContentBody(composeComponentsPanel(groupModel));
		addToContentBody(composeReformLink(groupModel));

	}

	protected Link composeReformLink(final IModel<NaniteGroup> naniteGroupModel) {
		Link link = new Link("reform") {

			/**
			 * 
			 */
			private static final long serialVersionUID = -213962075156991317L;

			@Override
			public void onClick() {
				getElysiumSession().setNamedEntity("naniteGroup", naniteGroupModel.getObject());
				setResponsePage(NaniteGroupReformPage.class);
			}
		};

		link.setVisible(getNanitesBPO().canReform(naniteGroupModel.getObject()));
		return link;
	}

	private Component composeComponentsPanel(ElysiumWicketModel<NaniteGroup> groupModel) {
		if (groupModel.getObject().getGroupLevel() == 0) {
			return createDummyLink("components", false, false);
		}
		ComponentsPanel panel = new ComponentsPanel("components", groupModel);

		return panel;
	}

	private Component composeUpgradeLink(ElysiumWicketModel<NaniteGroup> groupModel) {
		Link<NaniteGroup> link = new Link<NaniteGroup>("upgrade", groupModel) {

			/**
			 * 
			 */
			private static final long serialVersionUID = -4526206293884432294L;

			public void onClick() {
				getNanitesBPO().upgrade(getModelObject());
				setResponsePage(NaniteGroupShowPage.class);
			};
		};

		link.setVisible(getNanitesBPO().canUpgrade(groupModel.getObject()));
		return link;
	}

	private String getEnvStateString(NaniteGroup group) {

		List<String> messages = new LinkedList<String>();
		Gate gate = new GateBPO().getGateAt(group.getPosition().getEnvironment());

		if (gate == null) {
			messages.add(getLocalizedMessage("noGatePresent"));
		} else if (gate.getGatePass() != null) {
			messages.add(getLocalizedMessage("gateLocked"));
		}

		return messages.size() < 1 ? "" : messages.toString();
	}

	public Link<Gate> composeJumpLink(final ElysiumWicketModel<NaniteGroup> groupModel) {
		Link<Gate> link = new Link<Gate>("jumpGate") {
			public void onClick() {
				getElysiumSession().setNamedEntity(NE_NANITE_GROUP, groupModel.getObject());
				setResponsePage(UsePlanetaryGatePage.class);
			};
		};

		link.setVisible(getNanitesBPO().canJumpGate(groupModel.getObject()));
		return link;
	}

	private Component composeTitleText(NaniteGroup group) {
		String key = "naniteGroup";
		return new Label("titleText", new ResourceModel(key));
	}

	protected Link getDoubleCountLink(final ElysiumWicketModel<NaniteGroup> groupModel) {
		Link link = new Link("doubleCount") {

			/**
			 * 
			 */
			private static final long serialVersionUID = -213962075156991317L;

			@Override
			public void onClick() {
				getNanitesBPO().doubleCount(groupModel.getObject());
				setResponsePage(NaniteGroupShowPage.class);
			}
		};

		link.setVisible(getNanitesBPO().canRaiseNanitesCount(groupModel.getObject()));

		return link;
	}

	protected Link getSplitLink(final ElysiumWicketModel<NaniteGroup> groupModel) {
		Link link = new Link("split") {

			/**
			 * 
			 */
			private static final long serialVersionUID = -213962075156991317L;

			@Override
			public void onClick() {
				getNanitesBPO().split(groupModel.getObject(), true, false);
				setResponsePage(NaniteGroupShowPage.class);
			}
		};

		link.setVisible(getNanitesBPO().canSplit(groupModel.getObject(), true, false));
		return link;
	}

	private Collection<Component> composeQuickJumpItems(NaniteGroup group) {

		Collection<Component> result = new LinkedList<Component>();

		final ElysiumWicketModel<NaniteGroup> groupModel = new ElysiumWicketModel<NaniteGroup>(group);

		final String homeGateAdress = getAvatar().getHomeGateAdress();

		result.add(getJumpHomeLink(groupModel, homeGateAdress));

		result.add(getJumpArenaLink(groupModel));

		result.add(getJumpElysiumLink(groupModel));

		result.add(getExitLink(groupModel));

		return result;
	}

	private Component getExitLink(ElysiumWicketModel<NaniteGroup> groupModel) {
		Link exitLink = new Link<NaniteGroup>("exit", groupModel) {

			@Override
			public void onClick() {
				getNanitesBPO().exit(getModelObject());
				setResponsePage(NaniteGroupShowPage.class);
			}
		};

		exitLink.setVisible(getNanitesBPO().canExit(groupModel.getObject()));
		return exitLink;
	}

	protected Link<String> getJumpHomeLink(final ElysiumWicketModel<NaniteGroup> groupModel, final String homeGateAdress) {

		Gate gate = getGateBPO().getGateAt(groupModel.getObject().getPosition().getEnvironment());
		String currentGateAdress = gate == null ? "---" : gate.getGateAdress();

		Link<String> link = new Link<String>("jumpHome") {

			@Override
			public void onClick() {
				getNanitesBPO().gateTravel(groupModel.getObject(), homeGateAdress);
				setResponsePage(NaniteGroupShowPage.class);
			}
		};

		link.setEnabled(gate != null && !homeGateAdress.equals(currentGateAdress));
		link.setVisible(gate != null && getNanitesBPO().canJumpGate(groupModel.getObject()));

		return link;
	}

	protected Link<String> getJumpArenaLink(final ElysiumWicketModel<NaniteGroup> groupModel) {
		Link<String> link = new Link<String>("jumpArena") {

			@Override
			public void onClick() {
				getNanitesBPO().gateTravel(groupModel.getObject(), "arena");
				setResponsePage(NaniteGroupShowPage.class);

			}
		};

		Gate gate = getGateBPO().getGateAt(groupModel.getObject());
		String currentGateAdress = gate == null ? "---" : gate.getGateAdress();

		link.setEnabled(gate != null && !"arena".equals(currentGateAdress));
		link.setVisible(gate != null && getNanitesBPO().canJumpGate(groupModel.getObject()));

		return link;
	}

	protected Link<String> getJumpElysiumLink(final ElysiumWicketModel<NaniteGroup> groupModel) {
		Link<String> link = new Link<String>("jumpElysium") {

			@Override
			public void onClick() {
				getNanitesBPO().gateTravel(groupModel.getObject(), "elysium");
				setResponsePage(NaniteGroupShowPage.class);
			}
		};

		Gate gate = getGateBPO().getGateAt(groupModel.getObject());
		String currentGateAdress = gate == null ? "---" : gate.getGateAdress();
		link.setEnabled(gate != null && !"elysium".equals(currentGateAdress));
		link.setVisible(gate != null && getNanitesBPO().canJumpGate(groupModel.getObject()));

		return link;
	}

	private Component getOtherNanitesTable(NaniteGroup scanningGroup) {

		Iterator<NaniteGroup> nanites = getScannerBPO().scanForOtherNaniteGroups(scanningGroup).iterator();
		final ElysiumWicketModel<NaniteGroup> scanningGroupModel = new ElysiumWicketModel<NaniteGroup>(scanningGroup);

		final List<IModel> models = new ArrayList<IModel>();
		while (nanites.hasNext()) {
			models.add(new ElysiumWicketModel<NaniteGroup>(nanites.next()));
		}

		Component otherNanitesTable = new RefreshingView<NaniteGroup>("otherNanitesTable") {
			protected Iterator getItemModels() {

				return models.iterator();
			}

			protected void populateItem(Item item) {
				final ElysiumWicketModel<NaniteGroup> naniteGroupModel = (ElysiumWicketModel<NaniteGroup>) item
								.getModel();
				NaniteGroup otherGroup = (NaniteGroup) naniteGroupModel.getObject();

				Position position = otherGroup.getPosition();
				Gate gate = getGateBPO().getGateAt(position.getEnvironment());
				String gateCode = gate.getGateAdress();
				Link ownerLink = new Link("ownerLink") {

					@Override
					public void onClick() {
						getElysiumSession().setNamedEntity(NE_NANITE_GROUP, naniteGroupModel.getObject());
						setResponsePage(NaniteGroupShowPage.class);
					}
				};

				ownerLink.add(new Label("owner", new Model(otherGroup.getController().getName())));

				ownerLink.setEnabled(otherGroup.getController().equals(getAvatar()));
				item.add(ownerLink);

				NaniteGroup scanningGroup = scanningGroupModel.getObject();
				boolean canScanDetails = getScannerBPO().canScanDetails(scanningGroup, otherGroup);
				String leveltext = canScanDetails ? NumberFormat.getIntegerInstance().format(
								otherGroup.getController().getLevel()) : "???";
				item.add(new Label("level", new Model(leveltext)));

				String groupLevel = canScanDetails ? NumberFormat.getIntegerInstance().format(
								otherGroup.getGroupLevel()) : "???";
				item.add(new Label("groupLevel", new Model(groupLevel)));

				item.add(new Label("signature", new Model(NumberFormat.getNumberInstance().format(
								getScannerBPO().computeSignatureStrength(otherGroup)))));

				String countText = canScanDetails ? NumberFormat.getNumberInstance()
								.format(otherGroup.getNaniteCount()) : "???";

				item.add(new Label("count", new Model(countText)));

				item.add(getAttackCommandLink(scanningGroupModel, naniteGroupModel));

				item.add(getSendMessageCommandLink(naniteGroupModel));
				item.add(getEnterCommandLink(scanningGroupModel, naniteGroupModel));

			}

			private Component getEnterCommandLink(final ElysiumWicketModel<NaniteGroup> scanningGroupModel,
							final ElysiumWicketModel<NaniteGroup> container) {
				Link enterLink = new Link("enter") {
					@Override
					public void onClick() {
						new NaniteBPO().enter(scanningGroupModel.getObject(), container.getObject());
						setResponsePage(NaniteGroupShowPage.class);

					}
				};

				enterLink.setVisible(new NaniteBPO().canEnter(scanningGroupModel.getObject(), container.getObject()));

				return enterLink;
			}

			private Component getSendMessageCommandLink(final ElysiumWicketModel<NaniteGroup> naniteGroupModel) {
				Link link = new Link("sendMessage") {

					@Override
					public void onClick() {
						Avatar receiver = naniteGroupModel.getObject().getController();
						getElysiumSession().setNamedEntity("receiver", receiver);
						setResponsePage(MessageCreatePage.class);
						return;
					}
				};
				// only show if we are not me.
				link.setVisible(!naniteGroupModel.getObject().getController().equals(getAvatar()));
				return link;
			}

			private Component getAttackCommandLink(final ElysiumWicketModel<NaniteGroup> attackerModel,
							final ElysiumWicketModel<NaniteGroup> defenderModel) {
				AjaxLink link = new AjaxLink("attack") {


					@Override
					public void onClick(AjaxRequestTarget target) {

						NaniteGroup attacker = attackerModel.getObject();
						NaniteGroup defender = defenderModel.getObject();
						if (getNanitesBPO().canAttack(attacker, defender)) {
							getNanitesBPO().attack(attacker, defender);
							if (attacker.getNaniteCount() > 0) {
								getPage().setResponsePage(NaniteGroupShowPage.class);
							} else {
								getElysiumSession().setNamedEntity(NE_NANITE_GROUP, null);
								getPage().setResponsePage(NaniteGroupListPage.class);
							}
						} else {
							error("cannot attack");
						}
					}

				};

				link.setVisible(getNanitesBPO().canAttack(attackerModel.getObject(), defenderModel.getObject()));
				return link;
			}
		};

		return otherNanitesTable;
	}
}
