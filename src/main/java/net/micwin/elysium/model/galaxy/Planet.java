package net.micwin.elysium.model.galaxy;

import javax.persistence.Entity;

@Entity
public final class Planet extends Environment {

	public Planet() {
		super(Planet.class);
	}

	@Override
	public String toString() {
		return "Planet " + getPosition().composePath(null).toString();
	}

	@Override
	public String getName() {
		return "Planet " + getPosition().getX() + "/" + getPosition().getY() + " @ "
						+ getPosition().getEnvironment().getName();
	}
}
