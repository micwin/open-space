package net.micwin.elysium.dao;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import net.micwin.elysium.model.appliances.Utilization;
import net.micwin.elysium.model.characters.Avatar;
import net.micwin.elysium.model.replication.BluePrint;

public class HibernateBluePrintDao extends ElysiumHibernateDaoSupport<BluePrint> implements IBluePrintDao {

	@Override
	public BluePrint create(Avatar owner, String nameKey, Utilization... utilizations) {
		BluePrint bluePrint = new BluePrint();
		bluePrint.setName(nameKey);
		bluePrint.setOwner(owner);
		bluePrint.setUtilizations(Arrays.asList(utilizations));
		super.save(bluePrint);
		return bluePrint;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<BluePrint> findByController(Avatar avatar) {
		return lookupHql(" from BluePrint where owner.id=" + avatar.getId());
	}

	@Override
	public Class<BluePrint> getEntityClass() {
		return BluePrint.class;
	}

	@Override
	public BluePrint loadById(Serializable id) {
		return super.loadById((Long) id);
	}
}
