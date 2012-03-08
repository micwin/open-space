package net.micwin.elysium.model.characters;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToOne;

import net.micwin.elysium.model.ElysiumEntity;
import net.micwin.elysium.model.appliances.Utilization;
import net.micwin.elysium.model.galaxy.Position;
import net.micwin.elysium.view.storyline.StoryLineItem;

import org.hibernate.annotations.CollectionOfElements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.istack.internal.NotNull;

/**
 * Interface for specifying an avatar PC.
 * 
 * @author MicWin
 * 
 */
@Entity
public class Avatar extends ElysiumEntity {

	private static final Logger L = LoggerFactory.getLogger(Avatar.class);

	public static enum Personality {
		// MILITARY(StoryLineItem.LONE_WOLF),

		ENGINEER(StoryLineItem.NOT_REPRODUCABLE);

		// PRESERVER(StoryLineItem.NEW_HOPE);

		public static List<Personality> asList() {
			List<Personality> list = new LinkedList<Avatar.Personality>();
			list.add(ENGINEER);
			// list.add(MILITARY);
			// list.add(PRESERVER);
			return list;
		}

		private final StoryLineItem firstStoryItem;

		private Personality(StoryLineItem firstStoryItem) {
			this.firstStoryItem = firstStoryItem;
		}

		public StoryLineItem getFirstStoryItem() {
			return firstStoryItem;
		}

	}

	@OneToOne
	@NotNull
	private User controller;

	private Date creationDate;

	private int level;

	@NotNull
	private String name;

	@OneToOne
	private Organization organization;

	@Enumerated(EnumType.STRING)
	private Personality personality;

	@Embedded
	@NotNull
	private Position position;

	@Enumerated(EnumType.STRING)
	private StoryLineItem storyLineItem;

	private int talentPoints;

	@CollectionOfElements
	private Collection<Utilization> talents;

	private Long xp;

	public Avatar() {
		super(Avatar.class);
	}

	public User getController() {
		return controller;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public int getLevel() {
		return level;
	}

	public String getName() {
		return name;
	}

	public Organization getOrganization() {
		return organization;
	}

	public Personality getPersonality() {
		return personality;
	}

	public Position getPosition() {
		return position;
	}

	public StoryLineItem getStoryLineItem() {
		return storyLineItem;
	}

	public int getTalentPoints() {
		return talentPoints;
	}

	public Collection<Utilization> getTalents() {
		return talents;
	}

	public Long getXp() {
		return xp;
	}

	public void setController(User controller) {
		this.controller = controller;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	public void setPersonality(Personality personality) {
		this.personality = personality;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public void setStoryLineItem(StoryLineItem storyLineItem) {
		this.storyLineItem = storyLineItem;
	}

	public void setTalentPoints(int talentPoints) {
		this.talentPoints = talentPoints;
	}

	public void setTalents(Collection<Utilization> talents) {
		if (L.isDebugEnabled()) {
			L.debug("setting talents " + talents + " to avatar " + getName());
		}
		this.talents = talents;
	}

	public void setXp(Long xp) {
		this.xp = xp;
	}

}
