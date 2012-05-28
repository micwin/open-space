package net.micwin.elysium.view.collective;

import java.text.NumberFormat;

import net.micwin.elysium.bpo.GateBPO;
import net.micwin.elysium.bpo.NaniteBPO;
import net.micwin.elysium.model.NaniteGroup;
import net.micwin.elysium.view.BasePage;

import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.util.string.StringValue;

public class NaniteGroupShowPage extends BasePage {

	GateBPO gateBPO = new GateBPO();

	NaniteBPO naniteBPO = new NaniteBPO();

	public NaniteGroupShowPage() {
		super(true);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		ensureAvatarPresent();
		ensureLoggedIn();
		ensureStoryShown();

		StringValue groupIdString = getPageParameters().get("groupId");
		if (groupIdString.isEmpty() || groupIdString.isNull()) {

			throw new RestartResponseException(NaniteGroupListPage.class);
		}

		Long groupId = groupIdString.toLongObject();
		NaniteGroup group = naniteBPO.getNanitesDao().loadById(groupId);

		String gateCode = gateBPO.getGateAt(group.getPosition().getEnvironment()).getGateAdress();

		addToContentBody(new Label("groupPosition", "" + gateCode));
		addToContentBody(new Label("groupCount", NumberFormat.getIntegerInstance().format(group.getNaniteCount())));
		addToContentBody(new Label("groupState", "" + group.getState()));

	}

}
