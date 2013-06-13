package classifiers;

import weka.classifiers.Classifier;
import weka.core.Debug;

/**
 * Class for saving classifiers we've learned to file for reuse
 * @author Ophir De Jager
 *
 */
public class ClassifierSaver {

	/**
	 * Save classifier to file
	 * @param file file name
	 * @param classifier classifier
	 * @return
	 */
	public static boolean saveClassifier(String file, Classifier classifier){
		return Debug.saveToFile(file, classifier);
	}
	
}
