package net.micwin.elysium.model.galaxy;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

/**
 * A sector of space, spanning 100 x 100 lightyears.
 * 
 * @author MicWin
 * 
 */
@SuppressWarnings("rawtypes")
@Entity
public class Sector extends Environment {

	@OneToMany(cascade = CascadeType.ALL)
	private List<SolarSystem> solarSystems = new LinkedList<SolarSystem>();

	private int systemCount;

	public Sector() {
		super(Sector.class);
	}

	public void setSolarSystems(List<SolarSystem> solarSystems) {
		this.solarSystems = solarSystems;
		setSystemCount(solarSystems.size());
	}

	public List<SolarSystem> getSolarSystems() {
		return solarSystems;
	}

	public void addSolarSystem(SolarSystem solarSystem) {
		solarSystems.add(solarSystem);
		setSystemCount(solarSystems.size());

	}

	public void setSystemCount(int systemCount) {
		this.systemCount = systemCount;
	}

	public int getSystemCount() {
		return systemCount;
	}

	@Override
	public String getName() {
		return "Sector " + getPosition().getX() + "/" + getPosition().getY();
	}
}
