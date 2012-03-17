package net.micwin.elysium.model.characters;

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

/**
 * Interface for specifying an avatar PC.
 * 
 * @author MicWin
 * 
 */
@Entity
public class Avatar extends ElysiumEntity {

	private static final Logger L = LoggerFactory.getLogger(Avatar.class);

	public static enum Race {
		// MILITARY(StoryLineItem.LONE_WOLF),

		NANITE(StoryLineItem.BOOTING);

		// PRESERVER(StoryLineItem.NEW_HOPE);

		public static List<Race> asList() {
			List<Race> list = new LinkedList<Avatar.Race>();
			list.add(NANITE);
			// list.add(MILITARY);
			// list.add(PRESERVER);
			return list;
		}

		private final StoryLineItem firstStoryItem;

		private Race(StoryLineItem firstStoryItem) {
			this.firstStoryItem = firstStoryItem;
		}

		public StoryLineItem getFirstStoryItem() {
			return firstStoryItem;
		}

	}

	@OneToOne
	private User controller;

	private Date creationDate;

	private int level;

	private String name;

	@OneToOne
	private Organization organization;

	@Enumerated(EnumType.STRING)
	private Race race;

	@Embedded
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

	public Race getPersonality() {
		return race;
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

	public void setPersonality(Race race) {
		this.race = race;
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
