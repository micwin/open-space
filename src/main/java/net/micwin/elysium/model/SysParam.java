package net.micwin.elysium.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.UniqueConstraint;

/**
 * A sysparam is a persistent value that musst get transported between shutdown
 * and startup.
 * 
 * @author MicWin
 * 
 */
@Entity
public class SysParam extends ElysiumEntity {

	public SysParam() {
		super(SysParam.class);
	}

	@Column(unique = true)
	String key;
	String value;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
