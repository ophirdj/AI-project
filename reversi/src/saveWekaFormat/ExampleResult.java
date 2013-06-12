package saveWekaFormat;

import java.util.Map;

/**
 * Class for representing an example classified for all players
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
