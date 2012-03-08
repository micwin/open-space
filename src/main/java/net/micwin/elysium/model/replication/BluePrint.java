package net.micwin.elysium.model.replication;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import net.micwin.elysium.model.ElysiumEntity;
import net.micwin.elysium.model.appliances.Utilization;
import net.micwin.elysium.model.characters.Avatar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * a blueprint for a planetary base.
 * 
 * @author MicWin
 * 
 */
@Entity
public final class BluePrint extends ElysiumEntity {

	private static final Logger L = LoggerFactory.getLogger(BluePrint.class);

	private String name;

	@Enumerated(EnumType.STRING)
	private BluePrintType type;

	@OneToMany(cascade = CascadeType.ALL)
	private List<Utilization> utilizations;

	@OneToOne
	private Avatar owner;

	private int usageCounts;

	public BluePrint() {
		super(BluePrint.class);
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		if (name == null) {
			name = "unnamedBlueprint";
		}
		return name;
	}

	public void setUtilizations(List<Utilization> utilizations) {
		this.utilizations = utilizations;
	}

	public List<Utilization> getUtilizations() {
		if (utilizations == null) {
			utilizations = new LinkedList<Utilization>();
		}
		return utilizations;
	}

	long getComplexity() {
		return type.getComplexityCalculator().calculateComplexity(this);
	}

	public void setOwner(Avatar owner) {
		this.owner = owner;
	}

	public Avatar getOwner() {
		return owner;
	}

	public void setUsageCounts(int usageCounts) {
		this.usageCounts = usageCounts;
	}

	public int getUsageCounts() {
		return usageCounts;
	}
}
