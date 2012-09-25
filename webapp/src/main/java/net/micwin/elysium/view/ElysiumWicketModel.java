package net.micwin.elysium.view;

import java.util.LinkedList;
import java.util.List;

import net.micwin.elysium.dao.DaoManager;
import net.micwin.elysium.entities.ElysiumEntity;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

/**
 * a loadable detachable model for elysiumEntities.
 * 
 * @author MicWin
 * 
 * @param <E>
 */
public class ElysiumWicketModel<E extends ElysiumEntity> extends LoadableDetachableModel<E> {

	public static <E extends ElysiumEntity> ElysiumWicketModel<E> of(E entity) {

		return new ElysiumWicketModel<E>(entity);

	}

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
	 * Converts a list of elysium entities into a list of IModels wrapping these
	 * 
	 * @param <T>
	 * @param entities
	 * @return
	 */
	public static <T extends ElysiumEntity> List<IModel<T>> asModelList(List<T> entities) {
		List<IModel<T>> list = new LinkedList<IModel<T>>();

		for (T entity : entities) {
			list.add(new ElysiumWicketModel<T>(entity));
		}
		return list;
	}

}
