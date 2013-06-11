package saveWekaFormat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;

public class WekaEncoder {

	private enum IsFirstStateBetter {
		Yes, No;
	}

	private int numPlayers;
	private int numFeatures;
	private int numAttributes;
	private List<ArffSaver> savers;
	private List<Instances> instances;
	private List<Map<String, Attribute>> firstStateMapping;
	private List<Map<String, Attribute>> secondStateMapping;
	private List<Attribute> decisions;

	/**
	 * Initializes a new WEKA encoder
	 * 
	 * @param features
	 *            list of game's features names
	 * @param directoryPath
	 *            path to directory where files will be created
	 * @param playerNames
	 *            list of player names (like black/white)
	 * @throws IOException
	 *             if save files for data could not open
	 */
	public WekaEncoder(List<String> features, List<String> playerNames,
			String directoryPath) throws IOException {
		this.numPlayers = playerNames.size();
		numFeatures = features.size();
		numAttributes = 2 * numFeatures + 1;
		savers = new ArrayList<ArffSaver>(numPlayers);
		instances = new ArrayList<Instances>(numPlayers);
		firstStateMapping = new ArrayList<Map<String,Attribute>>(numPlayers);
		secondStateMapping = new ArrayList<Map<String,Attribute>>(numPlayers);
		decisions = new ArrayList<Attribute>(numPlayers);

		for (int player = 0; player < numPlayers; player++) {
			ArffSaver saver = new ArffSaver();
			saver.setDestination(new File(directoryPath + "/"
					+ playerNames.get(player) + ".arff"));
			saver.setFile(new File(directoryPath + "/" + playerNames.get(player)
					+ ".arff"));
			
			ArrayList<Attribute> attributes = createExtendedFeatureVector(features);
			
			Instances instances = new Instances("state comparison", attributes, 0);
			instances.setClassIndex(attributes.size() - 1);
			saver.setStructure(instances);
			saver.setCompressOutput(true);
			saver.setRetrieval(ArffSaver.INCREMENTAL);
			
			this.instances.add(instances);
			this.savers.add(saver);
		}
	}


	/**
	 * Create a list in the following structure:
	 * <features state 1><features state 2><is 1 better than 2>
	 * @param featureNames
	 * @return
	 */
	private ArrayList<Attribute> createExtendedFeatureVector(List<String> featureNames) {
		ArrayList<Attribute> attInfo = new ArrayList<Attribute>();
		Map<String, Attribute> firstMap = new HashMap<String, Attribute>(numFeatures);
		Map<String, Attribute> secondMap = new HashMap<String, Attribute>(numFeatures);
		for (String attName : featureNames){
			Attribute att = new Attribute(attName + " (state 1)");
			firstMap.put(attName, att);
			attInfo.add(att);
		}
		for (String attName : featureNames){
			Attribute att = new Attribute(attName + " (state 2)");
			secondMap.put(attName, att);
			attInfo.add(att);
		}
		ArrayList<String> vals = new ArrayList<String>();
		vals.add(IsFirstStateBetter.Yes.toString());
		vals.add(IsFirstStateBetter.No.toString());
		Attribute att = new Attribute("is first state better?", vals);
		decisions.add(att);
		attInfo.add(att);
		firstStateMapping.add(firstMap);
		secondStateMapping.add(secondMap);
		return attInfo;
	}

	/**
	 * Encode data to WEKA file format
	 * @param fv1
	 *            feature vector of state 1
	 * @param fv2
	 *            feature vector of state 2
	 * @param results
	 *            for each player specifies if the first state is better
	 */
	public void encode(Map<String, Double> fv1, Map<String, Double> fv2, boolean[] results) {
		assert (results.length == numPlayers);
		assert (fv1.size() == numFeatures);
		assert (fv2.size() == numFeatures);
		for (int i = 0; i < numPlayers; i++) {
			Instances instances = this.instances.get(i);
			ArffSaver saver = this.savers.get(i);
			Instance instance = new DenseInstance(numAttributes);
			instance.setDataset(instances);
			for(Map.Entry<String, Double> e: fv1.entrySet()){
				instance.setValue(firstStateMapping.get(i).get(e.getKey()), e.getValue());
			}
			for(Map.Entry<String, Double> e: fv2.entrySet()){
				instance.setValue(secondStateMapping.get(i).get(e.getKey()), e.getValue());
			}
//			for (int j = 0; j < numFeatures; j++) {
//				double d1 = fv1[j];
//				double d2 = fv2[j];
//				instance.setValue(j, d1);
//				instance.setValue(j + numFeatures, d2);
//			}
			instance.setValue(decisions.get(i),
					(results[i] ? IsFirstStateBetter.Yes.toString()
							: IsFirstStateBetter.No.toString()));
			try {saver.writeIncremental(instance);} catch (IOException e) {}
		}
	}

	/**
	 * Terminates savings & close files
	 * @throws IOException
	 */
	public void endSave() {
		for(ArffSaver saver: savers)
			try {
				saver.writeIncremental(null);
			} catch (IOException e) {
				e.printStackTrace();
			}
	}

}
