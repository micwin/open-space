package net.micwin.elysium.view;

import net.micwin.elysium.dao.DaoManager;
import net.micwin.elysium.entities.ElysiumEntity;

import org.apache.wicket.model.LoadableDetachableModel;

/**
 * a loadable detachable model for elysiumEntities.
 * 
 * @author MicWin
 * 
 * @param <E>
 */
public class ElysiumWicketModel<E extends ElysiumEntity> extends LoadableDetachableModel<E> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 422698831001094350L;
	private Class<E> baseClass;
	private Long id;

	public ElysiumWicketModel(E entity) {
		baseClass = entity.getBaseClass();
		id = entity.getId();
	}

	@Override
	protected E load() {
		return (E) DaoManager.I.getDao(baseClass).loadById(id);
	}

	/**
	 * Returns the model object as an elysium entity.
	 * 
	 * @return
	 * @deprecated
	 */
	public E getEntity() {
		return (E) getObject();
	}

	@Override
	public E getObject() {
		return load();
	}

	@Override
	public void setObject(E object) {
		super.setObject(null);
	}
}
