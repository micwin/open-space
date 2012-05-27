package net.micwin.elysium.view;

import net.micwin.elysium.dao.DaoManager;
import net.micwin.elysium.model.ElysiumEntity;

import org.apache.wicket.model.LoadableDetachableModel;

public class ElysiumLoadableDetachableModel<E extends ElysiumEntity> extends LoadableDetachableModel<Object> {

	private Class<ElysiumEntity> baseClass;
	private Long id;

	public ElysiumLoadableDetachableModel(E entity) {
		baseClass = entity.getBaseClass();
		id = entity.getId();
	}

	@Override
	protected E load() {
		return (E) DaoManager.I.getDao(baseClass).loadById(id);
	}

}
