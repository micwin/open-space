package net.micwin.elysium.model.characters;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

import net.micwin.elysium.model.ElysiumEntity;

@Entity
public final class Organization extends ElysiumEntity {

	@OneToOne
	private Avatar controller;

	public Organization() {
		super(Organization.class);
	}

	public void setController(Avatar controller) {
		this.controller = controller;
	}

	public Avatar getController() {
		return controller;
	}

}
