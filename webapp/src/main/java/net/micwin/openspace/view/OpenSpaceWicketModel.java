package net.micwin.openspace.view;

import java.util.LinkedList;
import java.util.List;

import net.micwin.openspace.dao.DaoManager;
import net.micwin.openspace.entities.OpenSpaceEntity;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

/**
 * a loadable detachable model for elysiumEntities.
 * 
 * @author MicWin
 * 
 * @param <E>
 */
public class OpenSpaceWicketModel<E extends OpenSpaceEntity> extends LoadableDetachableModel<E> {

	public static <E extends OpenSpaceEntity> OpenSpaceWicketModel<E> of(E entity) {

		return new OpenSpaceWicketModel<E>(entity);

	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 422698831001094350L;
	private Class<E> baseClass;
	private Long id;

	public OpenSpaceWicketModel(E entity) {
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
	public static <T extends OpenSpaceEntity> List<IModel<T>> asModelList(List<T> entities) {
		List<IModel<T>> list = new LinkedList<IModel<T>>();

		for (T entity : entities) {
			list.add(new OpenSpaceWicketModel<T>(entity));
		}
		return list;
	}

}
