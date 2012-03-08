package net.micwin.elysium.model.replication;

/**
 * A strategy that compute the complexity of a specific blue print depenmding on
 * its type.
 * 
 * @author MicWin
 * 
 */
public interface IComplexityCalculator {

	/**
	 * computes the complexity of the passed blueprint.
	 * 
	 * @param bluePrint
	 */
	long calculateComplexity(BluePrint bluePrint);

}
