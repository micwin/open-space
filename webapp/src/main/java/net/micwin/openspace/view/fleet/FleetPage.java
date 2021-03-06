package net.micwin.openspace.view.fleet;

import net.micwin.openspace.entities.fleet.Fleet;
import net.micwin.openspace.view.BasePageView;
import net.micwin.openspace.view.OpenSpaceWicketModel;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;

/**
 * Page showing list of or single fleet.
 * 
 * @author MicWin
 * 
 */
public class FleetPage extends BasePageView {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7373524745305177270L;
	private OpenSpaceWicketModel<Fleet> fleetModel;

	public FleetPage() {
		this(null);
	}

	public FleetPage(Fleet fleet) {
		super(false);
		this.fleetModel = fleet != null ? OpenSpaceWicketModel.of(fleet) : null;
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		ensureAvatarPresent(true);
		addToContentBody(composeFleetList());
		addToContentBody(composeFleetDetail());
	}

	private Component composeFleetDetail() {
		return new Label("fleetDetail", getLocalizedMessage("fleetDetail.title"));
	}

	private Component composeFleetList() {

		return new Label("fleetList", getLocalizedMessage("fleetList.title"));

	}

}
