package net.micwin.elysium.model.gates;

import javax.persistence.Embedded;
import javax.persistence.Entity;

import net.micwin.elysium.model.ElysiumEntity;
import net.micwin.elysium.model.galaxy.Position;

@Entity
public class Gate extends ElysiumEntity {

	@Embedded
	private Position position;

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
}
