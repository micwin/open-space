package net.micwin.elysium.entities.colossus;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import net.micwin.elysium.entities.ElysiumEntity;
import net.micwin.elysium.entities.galaxy.Position;
import net.micwin.elysium.messaging.IMessageEndpoint;

/**
 * A colossus is a structure built out of immobile Nanites. Some can carry other
 * naniteGroups inside and act as a transporter. They must be built over time
 * and dont just pop up.
 * 
 * @author MicWin
 * 
 */
@Entity
public class Colossus extends ElysiumEntity implements IMessageEndpoint {

	public static enum ColossusState {

		BUILDING_REPAIRING, ACTIVE;
	}

	private static final double BASE_STRUCTURE_POINTS = 10000;

	public static final long MIN_MAINETANCE_NANITES = 10000;

	@Enumerated(EnumType.STRING)
	private ColossusType colossusType;

	private int colossusLevel = 1;

	private int armorPoints = 0;

	@Enumerated(EnumType.STRING)
	private ColossusState state;

	@Embedded
	private Position position;

	private long structurePoints;

	private int battleCounter;

	private int mainetanceNanites;

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

	public long getMaxStructurePoints() {
		return (long) Math.pow(BASE_STRUCTURE_POINTS, colossusLevel);
	}

	@Override
	public String getEndPointId() {
		return "COL#" + getId();
	}

	public void setBattleCounter(int battleCounter) {

		this.battleCounter = battleCounter;
	}

	public int getBattleCounter() {
		return battleCounter;
	}

	public void setMainetanceNanites(int mainetanceNanites) {
		this.mainetanceNanites = mainetanceNanites;
	}

	public int getMainetanceNanites() {
		return mainetanceNanites;
	}

	public void setArmorPoints(int armorPoints) {
		this.armorPoints = armorPoints;
	}

	public int getArmorPoints() {
		return armorPoints;
	}

}
