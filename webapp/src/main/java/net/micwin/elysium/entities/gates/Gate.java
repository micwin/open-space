package net.micwin.elysium.entities.gates;

import javax.persistence.Embedded;
import javax.persistence.Entity;

import net.micwin.elysium.entities.ElysiumEntity;
import net.micwin.elysium.entities.galaxy.Position;

@Entity
public class Gate extends ElysiumEntity {

	@Embedded
	private Position position;

	private String gateAdress;

	public Gate() {
	}

	@Override
	public Class getBaseClass() {
		return Gate.class;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public Position getPosition() {
		return position;
	}

	public void setGateAdress(String gateAdress) {
		this.gateAdress = gateAdress;
	}

	public String getGateAdress() {
		return gateAdress;
	}

	@Override
	public String toString() {
		return gateAdress + " @ " + position;
	}
}
