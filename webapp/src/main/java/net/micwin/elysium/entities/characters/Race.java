package net.micwin.elysium.entities.characters;

import java.util.LinkedList;
import java.util.List;

import net.micwin.elysium.entities.appliances.Appliance;
import net.micwin.elysium.entities.appliances.Utilization;
import net.micwin.elysium.view.storyline.StoryLineItem;

public enum Race {
	// MILITARY(StoryLineItem.LONE_WOLF),

	NANITE(null, 1, Utilization.Factory.create(Appliance.NANITE_MANAGEMENT, 1, 30), Utilization.Factory.create(
					Appliance.NANITE_BATTLE, 0, 50),
					Utilization.Factory.create(Appliance.NANITE_DAMAGE_CONTROL, 0, 25), Utilization.Factory.create(
									Appliance.NANITE_CRITICAL_HIT, 0, 20), Utilization.Factory.create(
									Appliance.SCANNING, 0, 99));

	// PRESERVER(StoryLineItem.NEW_HOPE);

	public static List<Race> asList() {
		List<Race> list = new LinkedList<Race>();
		list.add(NANITE);
		// list.add(MILITARY);
		// list.add(PRESERVER);
		return list;
	}

	private final StoryLineItem firstStoryItem;
	private final int initialNanites;

	private final Utilization[] initialTalents;

	private Race(StoryLineItem firstStoryItem, int initialNanites, Utilization... initialTalents) {
		this.firstStoryItem = firstStoryItem;
		this.initialNanites = initialNanites;
		this.initialTalents = initialTalents;
	}

	public StoryLineItem getFirstStoryItem() {
		return firstStoryItem;
	}

	public int getInitialNanites() {
		return initialNanites;
	}

	public Utilization[] getInitialTalents() {
		return initialTalents;
	}

}