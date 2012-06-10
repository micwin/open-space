package net.micwin.elysium.view;

import net.micwin.elysium.dao.DaoManager;
import net.micwin.elysium.model.ElysiumEntity;

import org.apache.wicket.model.LoadableDetachableModel;

/**
 * a loadable detachable model for elysiumEntities.
 * 
 * @author MicWin
 * 
 * @param <E>
 */
public class ElysiumWicketModel<E extends ElysiumEntity> extends LoadableDetachableModel<Object> {

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
	 */
	public E getEntity() {
		return (E) getObject();
	}

}
