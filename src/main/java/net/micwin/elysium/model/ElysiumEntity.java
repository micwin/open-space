package net.micwin.elysium.model;

import java.io.Serializable;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

@MappedSuperclass
public abstract class ElysiumEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Transient
	private final Class<? extends ElysiumEntity> baseClass;

	/**
	 * Call this in your default constructor.
	 * 
	 * @param baseClass
	 *            the base class of this entity. Use explicit types, dont use
	 *            getClass() since this may return any enhanced class code.
	 */
	protected ElysiumEntity(Class<? extends ElysiumEntity> baseClass) {
		this.baseClass = baseClass;
	}

	public void setId(Serializable id) {
		this.id = (Long) id;
	}

	public Serializable getId() {
		return id;
	}

	/**
	 * the base class of this entity. Use explicit types, dont use getClass()
	 * since this may return any enhanced class code.
	 * 
	 * @return
	 */
	public Class getBaseClass() {
		return baseClass;
	}
}
