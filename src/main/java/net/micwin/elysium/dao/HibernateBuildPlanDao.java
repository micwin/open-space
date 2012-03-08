package net.micwin.elysium.dao;

import java.io.Serializable;
import java.util.List;

import net.micwin.elysium.model.characters.Avatar;
import net.micwin.elysium.model.replication.BuildPlan;

public class HibernateBuildPlanDao extends ElysiumHibernateDaoSupport<BuildPlan> implements IBuildPlanDao {

	@Override
	public Class<BuildPlan> getEntityClass() {
		return BuildPlan.class;
	}

	@Override
	public BuildPlan loadById(Serializable id) {
		return super.loadById((Long) id);
	}

	@Override
	public List<BuildPlan> findByAvatar(Avatar avatar) {

		return lookupHql("from BuildPlan where controller.id=" + avatar.getId());
	}
}
