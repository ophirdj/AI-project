package saveWekaFormat;

import java.util.Map;

/**
 * Class for representing an example classified for all players
 * The example is consisted of 2 feature vectors - 1 for each state,
 * and an array of booleans representing for each player if the first
 * state is better than the second state.
 * @author Ophir De Jager
 *
 */
public class ExampleResult{
	private Map<String, Double> featureVector1;
	private Map<String, Double> featureVector2;
	private boolean[] results;

	public ExampleResult(Map<String, Double> featureVector1, Map<String, Double> featureVector2, boolean[] results){
		this.featureVector1 = featureVector1;
		this.featureVector2 = featureVector2;
		this.results = results;
	}
	
	public Map<String, Double> getFeatureVector1() {
		return featureVector1;
	}

	public Map<String, Double> getFeatureVector2() {
		return featureVector2;
	}

	public boolean[] getResults() {
		return results;
	}
}
