package net.micwin.openspace.view.documentation;

import java.text.NumberFormat;

import net.micwin.openspace.bpo.NaniteBPO;
import net.micwin.openspace.entities.nanites.NaniteGroup;
import net.micwin.openspace.entities.nanites.NaniteState;
import net.micwin.openspace.view.BasePage;

import org.apache.wicket.markup.html.basic.Label;

public class DocumentationPage extends BasePage {

	public DocumentationPage() {
		super(false);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		addToContentBody(new Label("entrenchedSignatureFactor", NumberFormat.getNumberInstance().format(
						NaniteState.ENTRENCHED.getSignatureFactor())));
		addToContentBody(new Label("entrenchedDefenseFactor", NumberFormat.getNumberInstance().format(
						NaniteState.ENTRENCHED.getReceivingDamageFactor())));
		addToContentBody(new Label("entrenchedSensorsFactor", NumberFormat.getNumberInstance().format(
						NaniteState.ENTRENCHED.getSensorFactor())));
		addToContentBody(new Label("entrenchedCounterstrikeFactor", NumberFormat.getNumberInstance().format(
						NaniteState.ENTRENCHED.getCounterStrikeDamageFactor())));
		int level = getAvatar() != null ? getAvatar().getLevel() : 5;
		addToContentBody(new Label("level", NumberFormat.getIntegerInstance().format(level)));

		addToContentBody(new Label("entrenchingDuration", NumberFormat.getIntegerInstance().format(level * 5)));
		addToContentBody(new Label("attackFactor1", NumberFormat.getNumberInstance().format(
						new NaniteBPO().computeNumberBasedEfficiencyFactor(1))));
		addToContentBody(new Label("attackFactor10", NumberFormat.getNumberInstance().format(
						new NaniteBPO().computeNumberBasedEfficiencyFactor(10))));
		addToContentBody(new Label("attackFactor100", NumberFormat.getNumberInstance().format(
						new NaniteBPO().computeNumberBasedEfficiencyFactor(100))));
		addToContentBody(new Label("attackFactor1000", NumberFormat.getNumberInstance().format(
						new NaniteBPO().computeNumberBasedEfficiencyFactor(1000))));
		addToContentBody(new Label("attackFactor10000", NumberFormat.getNumberInstance().format(
						new NaniteBPO().computeNumberBasedEfficiencyFactor(10000))));
		addToContentBody(new Label("attackFactor1Mio", NumberFormat.getNumberInstance().format(
						new NaniteBPO().computeNumberBasedEfficiencyFactor(1000000))));
		addToContentBody(new Label("attackFactorMax", NumberFormat.getNumberInstance().format(
						new NaniteBPO().computeNumberBasedEfficiencyFactor(Integer.MAX_VALUE))));

		addToContentBody(new Label("maxNanitesGroupSize", NumberFormat.getNumberInstance().format(
						NaniteGroup.MAX_NANITES_COUNT)));
		addToContentBody(new Label("noobLevel", "" + NaniteBPO.MAX_NOOB_LEVEL));

	}
}
