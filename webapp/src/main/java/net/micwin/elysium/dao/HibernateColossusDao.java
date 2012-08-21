package net.micwin.elysium.dao;

import java.util.Collection;

import net.micwin.elysium.entities.NaniteGroup;
import net.micwin.elysium.entities.characters.Avatar;
import net.micwin.elysium.entities.colossus.Colossus;
import net.micwin.elysium.entities.colossus.Colossus.ColossusState;
import net.micwin.elysium.entities.galaxy.Position;

import org.hibernate.SessionFactory;

public class HibernateColossusDao extends ElysiumHibernateDaoSupport<Colossus> implements IColossusDao {

	protected HibernateColossusDao(SessionFactory sf) {
		super(sf);
	}

	@Override
	public Class<Colossus> getEntityClass() {
		return Colossus.class;
	}

	@Override
	public Colossus convert(NaniteGroup naniteGroup) {
		Colossus colossus = new Colossus();
		colossus.setController(naniteGroup.getController());
		colossus.setPosition(naniteGroup.getPosition());
		colossus.setState(ColossusState.BUILDING_REPAIRING);
		colossus.setBattleCounter(naniteGroup.getBattleCounter());
		colossus.setName(naniteGroup.getName());
		colossus.setMainetanceNanites((int) Math.max(Colossus.MIN_MAINETANCE_NANITES, naniteGroup.getNaniteCount()));
		insert(colossus);
		return colossus;
	}

	@Override
	public Collection<Colossus> findByState(ColossusState state) {
		return super.lookupHql("from " + getEntityClass().getSimpleName() + " where state='" + state.name() + "'");
	}

}
