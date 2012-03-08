package net.micwin.elysium.model.appliances;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import net.micwin.elysium.model.ElysiumEntity;

/**
 * A utilization is a usage of a technology for specific appliances.
 * 
 * @author MicWin
 * 
 */
@Entity
public final class Utilization extends ElysiumEntity {

	public static class Factory {

		/**
		 * Creates a new utilization instance. Does, of course, NOT persist!
		 * 
		 * @param appliance
		 * @param level
		 * @return
		 */
		public static Utilization create(Appliance appliance, int level) {
			Utilization u = new Utilization();
			u.setAppliance(appliance);
			u.setLevel(level);
			return u;
		}
	};

	@Enumerated(EnumType.STRING)
	private Appliance appliance;

	private int level;

	@Column(name = "CUONT")
	private int count;

	public Utilization() {
		super(Utilization.class);
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getLevel() {
		return level;
	}

	public void setAppliance(Appliance appliance) {
		this.appliance = appliance;
	}

	public Appliance getAppliance() {
		return appliance;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getCount() {
		return count;
	}
}
