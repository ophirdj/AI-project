package classifiers;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import weka.core.Instances;

/**
 * Class for loading examples from file
 * @author Ophir De Jager
 *
 */
public class LoadExamples {

	/**
	 * Load examples from a file
	 * @param file
	 * @return
	 */
	public static Instances loadExamples(String file){
		try {
			Instances examples = new Instances(new FileReader(file));
			examples.setClassIndex(examples.numAttributes()-1);
			assert(examples.attribute(examples.numAttributes()-1).isNominal());
			return examples;
			}
		catch (FileNotFoundException e) {}
		catch (IOException e) {}
		return null;
	}
	
}
