package net.micwin.openspace.view;

import org.apache.wicket.markup.html.link.Link;

/**
 * A link that does nothing when clicked upon it.
 * 
 * @author MicWin
 * 
 */
public class EmptyLink extends Link {

	public EmptyLink(String id) {
		super(id);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		setEnabled(false);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -124429582509576663L;

	@Override
	public void onClick() {

		// do nothing
	}

}
