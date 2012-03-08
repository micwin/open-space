package net.micwin.elysium.model.replication;

import net.micwin.elysium.model.appliances.Utilization;

/**
 * A complexity calculator to compute complexity upon the assumption that each
 * element adds not only complexity to the whole. but raises complexity to add
 * another element.
 * 
 * @author MicWin
 * 
 */
public class SimpleComplexityCalculator implements IComplexityCalculator {

	private final long base;

	public SimpleComplexityCalculator(long base) {
		this.base = base;
	}

	@Override
	public long calculateComplexity(BluePrint bluePrint) {

		long componentCount = 0;
		long complexitySum = 0;

		for (Utilization utilization : bluePrint.getUtilizations()) {
			componentCount += utilization.getCount();
			complexitySum += utilization.getAppliance().getBaseComplexity() * utilization.getLevel();
		}

		return Math.round(complexitySum * Math.pow(1.01, componentCount));
	}

}
