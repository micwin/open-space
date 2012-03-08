package net.micwin.elysium.model.replication;

import javax.persistence.Entity;

import net.micwin.elysium.model.ElysiumEntity;

@Entity
public class Component extends ElysiumEntity {

	protected Component() {
		super(Component.class);
	}
}
