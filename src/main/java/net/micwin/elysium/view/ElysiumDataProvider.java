package net.micwin.elysium.view;

import java.io.Serializable;
import java.util.Iterator;

import net.micwin.elysium.ILoadEntityStrategy;
import net.micwin.elysium.dao.DaoManager;
import net.micwin.elysium.model.ElysiumEntity;

import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

public class ElysiumDataProvider<E extends ElysiumEntity> implements IDataProvider<E> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3976918077477639320L;

	private final ILoadEntityStrategy<E> loadEntityStrategy;

	public ElysiumDataProvider(ILoadEntityStrategy<E> loadEntityStrategy) {
		this.loadEntityStrategy = loadEntityStrategy;
	}
	
	@Override
	public void detach() {

	}
	
	
	@Override
	public Iterator<? extends E> iterator(int first, int count) {
		return loadEntityStrategy.load().subList(first, first + count).iterator();
	}

	@Override
	public int size() {
		return loadEntityStrategy.load().size();
	}

	@Override
	public IModel<E> model(E object) {
		final Class eClass = object.getBaseClass();
		final Serializable id = object.getId();
		return new LoadableDetachableModel<E>() {
			@Override
			protected E load() {
				return (E) DaoManager.I.getDao(eClass).loadById(id);
			}
		};
	}

}
