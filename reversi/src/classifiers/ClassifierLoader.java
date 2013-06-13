package classifiers;

import java.io.File;

import weka.classifiers.Classifier;
import weka.classifiers.misc.SerializedClassifier;

/**
 * Class for loading classifiers we've saved earlier
 * @author Ophir De Jager
 *
 */
public class ClassifierLoader {

	/**
	 * Load classifier from file
	 * @param file file name
	 * @return
	 */
	public static Classifier loadClassifier(String file){
		SerializedClassifier c = new SerializedClassifier();
		c.setModelFile(new File(file));
		return c;
	}
	
}
