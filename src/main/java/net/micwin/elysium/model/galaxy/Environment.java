package net.micwin.elysium.model.galaxy;

import javax.persistence.Embedded;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import net.micwin.elysium.model.ElysiumEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@MappedSuperclass
public abstract class Environment extends ElysiumEntity {

	private static final Logger L = LoggerFactory.getLogger(Environment.class);

	@Embedded
	private Position position;

	private int width;

	private int height;

	protected Environment(Class baseClass) {
		super(baseClass);
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getWidth() {
		return width;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getHeight() {
		return height;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public Position getPosition() {
		return position;
	}

	@Transient
	public abstract String getName();
}
