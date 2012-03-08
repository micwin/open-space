package net.micwin.elysium.model.galaxy;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;

import org.hibernate.annotations.Any;
import org.hibernate.annotations.AnyMetaDef;
import org.hibernate.annotations.MetaValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A position of something in an environment.
 * 
 * @author MicWin
 * 
 */
@Embeddable
public class Position {

	private static final Logger L = LoggerFactory.getLogger(Position.class);

	@Any(metaColumn = @Column(name = "env_type"), fetch = FetchType.EAGER)
	@AnyMetaDef(idType = "long", metaType = "string", metaValues = {
					@MetaValue(value = "S", targetEntity = Sector.class),
					@MetaValue(value = "P", targetEntity = Planet.class),
					@MetaValue(value = "Y", targetEntity = SolarSystem.class)

	})
	@JoinColumn(name = "env_id")
	private Environment environment;

	private int x;
	private int y;

	public Position() {
	}

	public Position(Environment environment, int x, int y) {
		this.environment = environment;
		this.x = x;
		this.y = y;
	}

	public Environment getEnvironment() {
		return environment;
	}

	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public List<String> composePath(List<String> pathTarget) {

		if (pathTarget == null) {
			pathTarget = new LinkedList<String>();
		}

		// insert parents
		if (getEnvironment() != null) {
			getEnvironment().getPosition().composePath(pathTarget);
		}

		pathTarget.add("" + x + "/" + y);

		return pathTarget;
	}

	@Override
	public String toString() {
		Environment env = getEnvironment();

		String toString = env.getName() == null ? "Galaxy" : env.getName();
		toString = "[" + getX() + "/" + getY() + "] @ " + toString;

		return toString;
	}
}
