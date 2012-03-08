package net.micwin.elysium.model.replication;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import net.micwin.elysium.model.galaxy.Planet;
import net.micwin.elysium.model.galaxy.Sector;

import org.junit.Test;

public class BluePrintTypeTest {

	@Test
	public void testAcceptsTarget() {
		assertTrue(BluePrintType.PLANETARY_BASE.acceptsTarget(Planet.class));
		assertFalse(BluePrintType.PLANETARY_BASE.acceptsTarget(Sector.class));
	}

}
