package net.micwin.elysium.model;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

import net.micwin.elysium.model.galaxy.Environment;

/**
 * A pile of nanites.
 * 
 * @author MicWin
 * 
 */
@Entity
public class NanoBotHive extends ElysiumEntity {

	protected NanoBotHive() {
		super(NanoBotHive.class);
	}

	@OneToOne
	Environment environment;

	public Environment getEnvironment() {
		return environment;
	}

	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

}
