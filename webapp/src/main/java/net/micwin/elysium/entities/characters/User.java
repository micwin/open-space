package net.micwin.elysium.entities.characters;

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

 */

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import net.micwin.elysium.entities.ElysiumEntity;
import net.micwin.elysium.messaging.IMessageEndpoint;

/**
 * An elysium user.
 * 
 * @author MicWin
 * 
 */

@Entity
public final class User extends ElysiumEntity implements Serializable, IMessageEndpoint {

	public static enum Role {
		/**
		 * Ø Server-Admin
		 */
		ADMIN,

		/**
		 * a game master.
		 */
		GAME_MASTER,

		/**
		 * A player.
		 */
		ACTOR;

		@Override
		public String toString() {
			return name();
		};
	}

	public static enum State {

		/**
		 * User has been registered and cobnfirmation E-mail has not yet been
		 * acknowledged.
		 */
		IN_REGISTRATION,

		/**
		 * user E-Mail has been acknowledged and user may operate accordingly to
		 * its role(s).
		 */
		ACTIVE,

		/**
		 * User may not operate. Units in game are not protected and may be
		 * robbed/ hacked/looted. Avatar may get attacked.
		 */
		PASSIVATED,

		/**
		 * User has been paused. Unit in game are completely protected.
		 */
		PAUSED,

		/**
		 * Signals that this user is an npc holder, ie a machine user, not a
		 * person.
		 */
		NPC;
	}

	private String login;

	private String pass;

	private State state;

	private Role role;

	private String skin;

	private Date lastLoginDate;

	public void setRole(Role role) {
		this.role = role;
	}

	public User() {
	}

	public User(String login, String pass, State state, Role role) {
		this.login = login;
		this.pass = pass;
		this.state = state;
		this.role = role;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	@Column(unique = true, nullable = false)
	public String getLogin() {
		return login;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	public String getPass() {
		return pass;
	}

	@Override
	public String toString() {
		return login;
	}

	@Enumerated(EnumType.STRING)
	public Role getRole() {
		return role;
	}

	public void setState(State state) {
		this.state = state;
	}

	@Enumerated(EnumType.STRING)
	public State getState() {
		return state;
	}

	public String getSkin() {
		return skin;
	}

	public void setSkin(String skin) {
		this.skin = skin;
	}

	@Override
	public Class getBaseClass() {
		return User.class;
	}

	public Date getLastLoginDate() {
		return lastLoginDate;
	}

	public void setLastLoginDate(Date newLastLoginDate) {
		lastLoginDate = newLastLoginDate;
	}

	@Override
	public String getEndPointId() {
		return IMessageEndpoint.TYPE_USER + login;
	}
}
