package net.micwin.elysium.model.characters;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import net.micwin.elysium.model.ElysiumEntity;

/**
 * An elysium user.
 * 
 * @author MicWin
 * 
 */

@Entity
public final class User extends ElysiumEntity implements Serializable {

	public static enum Role {
		/**Ø
		 * Server-Admin
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
		PAUSED;
	}

	private String login;

	private String pass;

	private State state;

	private Role role;

	public void setRole(Role role) {
		this.role = role;
	}

	public User() {
		super(User.class);
	}

	public User(String login, String pass, State state, Role role) {
		super(User.class);
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
}
