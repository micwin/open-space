package net.micwin.elysium.entities;

import static org.junit.Assert.*;

import net.micwin.elysium.entities.characters.Avatar;

import org.junit.Test;

public class NaniteGroupTest {

	@Test
	public void testEquals_NoIdSet() {

		NaniteGroup g1 = new NaniteGroup();

		// negative tests

		// check on null
		assertFalse(g1.equals(null));

		// check on another type
		assertFalse(g1.equals(new Integer(4)));

		// check on another Elysium Entity
		assertFalse(g1.equals(new Avatar()));

		// positive tests

		// check equals on same
		assertTrue(g1.equals(g1));

		// check equals with another group with id unset
		NaniteGroup g2 = new NaniteGroup();
		assertTrue(g1.equals(g2));

		// set one id
		g1.setId(15l);
		assertFalse(g1.equals(g2));
		assertFalse(g2.equals(g1));

		// set both id same value
		g2.setId(15l);
		assertTrue(g1.equals(g2));
		assertTrue(g2.equals(g1));

		// set both id different
		g2.setId(25l);
		assertFalse(g1.equals(g2));
		assertFalse(g2.equals(g1));

	}

	@Test
	public void testSetGetId() {
		NaniteGroup group = new NaniteGroup();
		group.setId(34l);
		assertEquals((long) 34, (long) group.getId());
	}

	@Test
	public void testHashCode() {

		NaniteGroup g1 = new NaniteGroup();
		NaniteGroup g2 = new NaniteGroup();
		assertEquals(g1.hashCode(), g2.hashCode());

		g1.setId(18l);
		g2.setId(237645239786l);
		assertTrue(g1.hashCode() != g2.hashCode());
	}
}
