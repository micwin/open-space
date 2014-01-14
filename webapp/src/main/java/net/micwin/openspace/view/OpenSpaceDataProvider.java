package net.micwin.openspace.view;

/*
 (c) 2012 micwin.net

 This file is part of open-space.

 open-space is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 open-space is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero Public License for more details.

 You should have received a copy of the GNU Affero Public License
 along with open-space.  If not, see http://www.gnu.org/licenses.

 Diese Datei ist Teil von open-space.

 open-space ist Freie Software: Sie können es unter den Bedingungen
 der GNU Affero Public License, wie von der Free Software Foundation,
 Version 3 der Lizenz oder (nach Ihrer Option) jeder späteren
 veröffentlichten Version, weiterverbreiten und/oder modifizieren.

 open-space wird in der Hoffnung, dass es nützlich sein wird, aber
 OHNE JEDE GEWÄHRLEISTUNG, bereitgestellt; sogar ohne die implizite
 Gewährleistung der MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK.
 Siehe die GNU Affero Public License für weitere Details.

 Sie sollten eine Kopie der GNU Affero Public License zusammen mit diesem
 Programm erhalten haben. Wenn nicht, siehe http://www.gnu.org/licenses. 

 */
import java.util.Iterator;

import net.micwin.openspace.ILoadEntityStrategy;
import net.micwin.openspace.dao.DaoManager;
import net.micwin.openspace.entities.OpenSpaceEntity;

import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

public class OpenSpaceDataProvider<E extends OpenSpaceEntity> implements IDataProvider<E> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3976918077477639320L;

	private final ILoadEntityStrategy<E> loadEntityStrategy;

	public OpenSpaceDataProvider(ILoadEntityStrategy<E> loadEntityStrategy) {
		this.loadEntityStrategy = loadEntityStrategy;
	}

	@Override
	public void detach() {

	}

	@Override
	public Iterator<? extends E> iterator(long first, long count) {
		return loadEntityStrategy.load().subList((int) first, (int) (first + count)).iterator();
	}

	@Override
	public long size() {
		return loadEntityStrategy.load().size();
	}

	@Override
	public IModel<E> model(E object) {
		final Class eClass = object.getBaseClass();
		final Long id = object.getId();
		return new LoadableDetachableModel<E>() {
			@Override
			protected E load() {
				return (E) DaoManager.I.getDao(eClass).loadById(id);
			}
		};
	}

}
