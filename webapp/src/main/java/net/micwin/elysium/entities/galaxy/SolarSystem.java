package net.micwin.elysium.entities.galaxy;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

@Entity
public class SolarSystem extends Environment {

	public SolarSystem() {
	}

	@OneToMany(cascade = CascadeType.ALL)
	private List<Planet> planets = new LinkedList<Planet>();

	@OneToOne
	private Planet mainPlanet;

	public Planet getMainPlanet() {
		return mainPlanet;
	}

	public void setPlanets(List<Planet> planets) {
		this.planets = planets;
	}

	public List<Planet> getPlanets() {
		return planets;
	}

	public void setMainPlanet(Planet planet) {
		this.mainPlanet = planet;
	}

	@Override
	public String getName() {
		return "SolarSystem " + getPosition().getX() + "/" + getPosition().getY() + " @ "
						+ getPosition().getEnvironment().getName();
	}

	@Override
	public Class getBaseClass() {
		return SolarSystem.class;
	}
}
