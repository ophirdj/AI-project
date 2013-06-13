package classifiers;

import weka.classifiers.Classifier;
import weka.classifiers.trees.J48;
import weka.core.Instances;

public class J48Pruned {

	public static Classifier buildFrom(Instances dataset) {
		if(dataset == null) return null;
		J48 j48 = new J48();
		j48.setUnpruned(false);
		j48.setMinNumObj(5);
		try {j48.buildClassifier(dataset);}
		catch (Exception e) {return null;}
		return j48;
	}

}
