package net.micwin.elysium.view.documentation;

import java.text.NumberFormat;

import org.apache.wicket.markup.html.basic.Label;

import net.micwin.elysium.entities.NaniteGroup.State;
import net.micwin.elysium.view.BasePage;

public class DocumentationPage extends BasePage {

	public DocumentationPage() {
		super(false);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		addToContentBody(new Label("entrenchedSignatureFactor", NumberFormat.getNumberInstance().format(
						State.ENTRENCHED.getSignatureFactor())));
		addToContentBody(new Label("entrenchedDefenseFactor", NumberFormat.getNumberInstance().format(
						State.ENTRENCHED.getReceivingDamageFactor())));
		addToContentBody(new Label("entrenchedSensorsFactor", NumberFormat.getNumberInstance().format(
						State.ENTRENCHED.getSensorFactor())));
		addToContentBody(new Label("entrenchedCounterstrikeFactor", NumberFormat.getNumberInstance().format(
						State.ENTRENCHED.getCounterStrikeDamageFactor())));
	}
}
