package net.micwin.elysium.entities;

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
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

import net.micwin.elysium.entities.appliances.Appliance;
import net.micwin.elysium.entities.characters.Avatar;
import net.micwin.elysium.entities.galaxy.Position;

/**
 * A pile of nanites.
 * 
 * @author MicWin
 * 
 */
@Entity
public class NaniteGroup extends ElysiumEntity {

	public enum State {

		IDLE(1.0, 1.0, 1.0, 1.0, true, 1.0, true, true), ENTRENCHING(0.0, 0.0, 1.1, 0.5, false, 0.9, true, false), ENTRENCHED(
						0.1, 0.1, 0.2, 10, false, 0.2, true, true);

		final double attackDamageFactor;
		final double counterStrikeDamageFactor;
		final double receivingDamageFactor;

		final double signatureFactor;
		final double sensorFactor;

		final boolean mayAttack;
		final boolean canRaiseNanitesCount;
		private boolean canSplit;

		private State(double pAttackDamageFactor, double pCounterStrikeFactor, double pSignatureFactor,
						double pSensorFactor, boolean pMayAttack, double pReceivingDamageFactor,
						boolean pCanRaisenanitesCount, boolean pCanSplit) {
			this.attackDamageFactor = pAttackDamageFactor;
			this.counterStrikeDamageFactor = pCounterStrikeFactor;
			this.signatureFactor = pSignatureFactor;
			this.sensorFactor = pSensorFactor;
			this.mayAttack = pMayAttack;
			this.receivingDamageFactor = pReceivingDamageFactor;
			this.canRaiseNanitesCount = pCanRaisenanitesCount;
			this.canSplit = pCanSplit;
		}

		public double getAttackDamageFactor() {
			return attackDamageFactor;
		}

		public double getCounterStrikeDamageFactor() {
			return counterStrikeDamageFactor;
		}

		public double getSignatureFactor() {
			return signatureFactor;
		}

		public double getSensorFactor() {
			return sensorFactor;
		}

		public boolean mayAttack() {
			return mayAttack;
		}

		public double getReceivingDamageFactor() {
			return receivingDamageFactor;
		}

		public boolean canRaiseNanitesCount() {
			return canRaiseNanitesCount;
		}

		public boolean canSplit() {

			return canSplit;
		}

	}

	public NaniteGroup() {
	}

	@Embedded
	private Position position;

	@Column
	private long naniteCount;

	@Enumerated(EnumType.STRING)
	private SupportMode supportMode = SupportMode.NONE;

	@Column
	private State state = State.IDLE;

	@Column
	private Date stateEndGT;

	public void setPosition(Position position) {
		this.position = position;
	}

	public Position getPosition() {
		return position;
	}

	public void setNaniteCount(long count) {
		this.naniteCount = count;

	}

	public long getNaniteCount() {
		return naniteCount;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
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

}
