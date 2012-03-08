package net.micwin.elysium.model.replication;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import net.micwin.elysium.model.ElysiumEntity;
import net.micwin.elysium.model.characters.Avatar;

@Entity
public class BuildPlan extends ElysiumEntity {

	@OneToOne
	private BluePrint blueprint;

	@OneToOne
	private Component componentInBuild;

	@OneToOne
	private Avatar controller;

	@OneToMany
	private List<Component> builtComponents ;

	public BuildPlan() {
		super(BuildPlan.class);
	}

	public void setBlueprint(BluePrint blueprint) {
		this.blueprint = blueprint;
	}

	public BluePrint getBlueprint() {
		return blueprint;
	}

	public void setComponentInBuild(Component componentInBuild) {
		this.componentInBuild = componentInBuild;
	}

	public Component getComponentInBuild() {
		return componentInBuild;
	}

	public void setController(Avatar controller) {
		this.controller = controller;
	}

	public Avatar getController() {
		return controller;
	}

	public List<Component> getBuiltComponents() {
		return builtComponents;
	}

	public void setBuiltComponents(List<Component> builtComponents) {
		this.builtComponents = builtComponents;
	}
}
