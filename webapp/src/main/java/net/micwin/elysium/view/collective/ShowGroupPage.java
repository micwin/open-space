package net.micwin.elysium.view.collective;

import java.text.NumberFormat;

import net.micwin.elysium.bpo.NaniteBPO;
import net.micwin.elysium.model.NaniteGroup;
import net.micwin.elysium.view.BasePage;

import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.util.string.StringValue;

public class ShowGroupPage extends BasePage {

	public ShowGroupPage() {
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

			throw new RestartResponseException(NaniteGroupsListPage.class);
		}

		Long groupId = groupIdString.toLongObject();
		NaniteBPO naniteBPO = new NaniteBPO();
		NaniteGroup group = naniteBPO.getNanitesDao().loadById(groupId);
		addToContentBody(new Label("groupPosition", "" + group.getPosition()));
		addToContentBody(new Label("groupCount", NumberFormat.getIntegerInstance().format(group.getNaniteCount())));
		addToContentBody(new Label("groupState", "" + group.getState()));

	}

}
