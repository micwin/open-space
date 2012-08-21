package net.micwin.elysium.entities.colossus;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import net.micwin.elysium.entities.ElysiumEntity;
import net.micwin.elysium.entities.galaxy.Position;


/**
 * A colossus is a structure built out of immobile Nanites. Some can carry other
 * naniteGroups inside and act as a transporter. They must be built over time
 * and dont just pop up.
 * 
 * @author MicWin
 * 
 */
@Entity
public class Colossus extends ElysiumEntity {

	public static enum ColossusState {
		
		BUILDING_REPAIRING, ACTIVE;
	}

	@Enumerated(EnumType.STRING)
	private ColossusType colossusType;

	private int colossusLevel;

	@Enumerated(EnumType.STRING)
	private ColossusState state;

	@Embedded
	private Position position;

	private long structurePoints ; 

	private long armorPoints ; 
	
	@Override
	public Class<Colossus> getBaseClass() {
		return Colossus.class;
	}

	public void setColossusType(ColossusType colossusType) {
		this.colossusType = colossusType;
	}

	public ColossusType getColossusType() {
		return colossusType;
	}

	public void setColossusLevel(int colossusLevel) {
		this.colossusLevel = colossusLevel;
	}

	public int getColossusLevel() {
		return colossusLevel;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public Position getPosition() {
		return position;
	}

	public void setState(ColossusState state) {
		this.state = state;
	}

	public ColossusState getState() {
		return state;
	}

	public void setStructurePoints(long structurePoints) {
		this.structurePoints = structurePoints;
	}

	public long getStructurePoints() {
		return structurePoints;
	}

	public void setArmorPoints(long armorPoints) {
		this.armorPoints = armorPoints;
	}

	public long getArmorPoints() {
		return armorPoints;
	}

}
