package net.micwin.elysium.entities.nanites;

/*
 (c) 2012 micwin.net

 This file is part of open-space.

 open-space is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 open-space is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero Public License for more details.

 You should have received a copy of the GNU Affero Public License
 along with open-space.  If not, see http://www.gnu.org/licenses.

 Diese Datei ist Teil von open-space.

 open-space ist Freie Software: Sie können es unter den Bedingungen
 der GNU Affero Public License, wie von der Free Software Foundation,
 Version 3 der Lizenz oder (nach Ihrer Option) jeder späteren
 veröffentlichten Version, weiterverbreiten und/oder modifizieren.

 open-space wird in der Hoffnung, dass es nützlich sein wird, aber
 OHNE JEDE GEWÄHRLEISTUNG, bereitgestellt; sogar ohne die implizite
 Gewährleistung der MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK.
 Siehe die GNU Affero Public License für weitere Details.

 Sie sollten eine Kopie der GNU Affero Public License zusammen mit diesem
 Programm erhalten haben. Wenn nicht, siehe http://www.gnu.org/licenses. 

 */import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import org.hibernate.cfg.annotations.Nullability;

import net.micwin.elysium.entities.SupportMode;
import net.micwin.elysium.entities.galaxy.Environment;
import net.micwin.elysium.entities.galaxy.Position;
import net.micwin.elysium.messaging.IMessageEndpoint;

/**
 * A pile of nanites.
 * 
 * @author MicWin
 * 
 */
@Entity
public class NaniteGroup extends Environment implements IMessageEndpoint {

	public static final long MAX_NANITES_COUNT = Integer.MAX_VALUE;

	private static final double BASE_MIN_NANITE_COUNT = 1000;

	@Column
	private long naniteCount;

	@Enumerated(EnumType.STRING)
	private SupportMode supportMode = SupportMode.NONE;

	@Column
	private NaniteState state = NaniteState.IDLE;

	@Column
	private NaniteState previousState = NaniteState.IDLE;

	@Column
	private Date stateEndGT;

	@Column(name = "battleCounter", nullable = false, columnDefinition = "int default 0")
	private int battleCounter = 0;

	@Column(name = "structurePoints", columnDefinition = "bigint default 0")
	private long structurePoints = 0;

	@Column(name = "groupLevel", columnDefinition = "int default 0")
	private int groupLevel;

	@Column(name = "catapults", columnDefinition = "int default 0")
	private int catapults;

	@Column(name = "satellites", columnDefinition = "int default 0")
	private int satellites;

	@Column(name = "flaks", columnDefinition = "int default 0")
	private int flaks;

	@Column(name = "naniteSlots", columnDefinition = "int default 0")
	private int naniteSlots;

	@Column(name = "ambushSquats", columnDefinition = "int default 0")
	private int ambushSquads;

	public NaniteGroup() {
		setElysium(true);
	}

	public void setNaniteCount(long count) {
		this.naniteCount = count;

	}

	public long getNaniteCount() {
		return naniteCount;
	}

	public NaniteState getState() {
		return state;
	}

	public void setState(NaniteState state) {
		if (this.state != previousState) {
			previousState = this.state;
		}
		this.state = state;
	}

	public void returnToPreviousState() {
		this.state = previousState;
	}

	@Override
	public Class getBaseClass() {
		return NaniteGroup.class;
	}

	public void setSupportMode(SupportMode supportMode) {
		this.supportMode = supportMode;
	}

	public SupportMode getSupportMode() {
		return supportMode;
	}

	public void setStateEndGT(Date stateEndGT) {
		this.stateEndGT = stateEndGT;
	}

	public Date getStateEndGT() {
		return stateEndGT;
	}

	public void raiseBattleCounter() {
		battleCounter++;
	}

	public void setBattleCounter(int battleCounter) {
		this.battleCounter = battleCounter;
	}

	public int getBattleCounter() {
		return battleCounter;
	}

	@Override
	public String getEndPointId() {
		return IMessageEndpoint.TYPE_NANITE_GROUP + getId();
	}

	@Override
	public String toString() {
		return getName();
	}

	public void setStructurePoints(long structurePoints) {
		this.structurePoints = structurePoints;
	}

	public long getStructurePoints() {
		return structurePoints;
	}

	@Override
	public boolean hasMailBox() {
		return false;
	}

	public int getGroupLevel() {
		return groupLevel;
	}

	public void setGroupLevel(int newGroupLevel) {
		this.groupLevel = newGroupLevel;
	}

	public long getMinNaniteCount() {
		if (groupLevel == 0)
			return 0;
		return (long) (BASE_MIN_NANITE_COUNT * Math.pow(1.2, groupLevel));
	}

	public void setCatapults(int catapults) {
		this.catapults = catapults;
	}

	public int getCatapults() {
		return catapults;
	}

	public int getNaniteSlots() {
		return naniteSlots;
	}

	public void setNaniteSlots(int naniteSlots) {
		this.naniteSlots = naniteSlots;
	}

	public void setPreviousState(NaniteState previousState) {
		this.previousState = previousState;
	}

	public NaniteState getPreviousState() {
		return previousState;
	}

	@Override
	public boolean needsPassivation() {
		return true;
	}

	public void setSatellites(int satellites) {
		this.satellites = satellites;
	}

	public int getSatellites() {
		return satellites;
	}

	public void setFlaks(int flaks) {
		this.flaks = flaks;
	}

	public int getFlaks() {
		return flaks;
	}

	public void setAmbushSquads(int ambushSquads) {
		this.ambushSquads = ambushSquads;
	}

	public int getAmbushSquads() {
		return ambushSquads;
	}
}
