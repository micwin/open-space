package net.micwin.elysium;

import java.io.Serializable;
import java.util.List;

import net.micwin.elysium.model.ElysiumEntity;

/**
 * Strategy to load a bunch of entities. For use in conjunction with anonymous
 * adapters et al.
 * 
 * @author MicWin
 * 
 */
public interface ILoadEntityStrategy<T extends ElysiumEntity> extends Serializable {

	/**
	 * Loadsthe entities qualified by this interface.
	 * 
	 * @return
	 */
	List<T> load();

}
