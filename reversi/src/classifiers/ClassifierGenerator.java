package classifiers;

import weka.classifiers.Classifier;
import weka.core.Instances;

/**
 * Interface for generating classifiers
 * @author Ophir De Jager
 *
 */
public interface ClassifierGenerator {

	/**
	 * Learn a new classifier from the data set
	 * @param dataset
	 * @return classifier from dataset
	 */
	Classifier learn(Instances dataset);
	
}
