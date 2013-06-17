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
	private List<Map<String, Attribute>> differenceMapping;
	private List<Map<String, Attribute>> divisionMapping;

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
		numAttributes = 4 * numFeatures + 1;
		savers = new ArrayList<ArffSaver>(numPlayers);
		instances = new ArrayList<Instances>(numPlayers);
		firstStateMapping = new ArrayList<Map<String,Attribute>>(numPlayers);
		secondStateMapping = new ArrayList<Map<String,Attribute>>(numPlayers);
		differenceMapping = new ArrayList<Map<String,Attribute>>(numPlayers);
		divisionMapping = new ArrayList<Map<String,Attribute>>(numPlayers);
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
	 * <features state 1><features state 2><features state 1-2><features state 1/2><is 1 better than 2>
	 * @param featureNames
	 * @return
	 */
	private ArrayList<Attribute> createExtendedFeatureVector(List<String> featureNames) {
		ArrayList<Attribute> attInfo = new ArrayList<Attribute>();
		Map<String, Attribute> firstMap = new HashMap<String, Attribute>(numFeatures);
		Map<String, Attribute> secondMap = new HashMap<String, Attribute>(numFeatures);
		Map<String, Attribute> differenceMap = new HashMap<String, Attribute>(numFeatures);
		Map<String, Attribute> divisionMap = new HashMap<String, Attribute>(numFeatures);
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
		for (String attName : featureNames){
			Attribute att = new Attribute(attName + " (state 1 - state 2)");
			differenceMap.put(attName, att);
			attInfo.add(att);
		}
		for (String attName : featureNames){
			Attribute att = new Attribute(attName + " (state 1 / state 2)");
			divisionMap.put(attName, att);
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
		differenceMapping.add(differenceMap);
		divisionMapping.add(divisionMap);
		return attInfo;
	}

	/**
	 * Encode a single example to WEKA file format
	 * @param example
	 */
	public synchronized void encode(ExampleResult example) {
		assert (example.getResults().length == numPlayers);
		assert (example.getFeatureVector1().size() == numFeatures);
		assert (example.getFeatureVector2().size() == numFeatures);
		for (int i = 0; i < numPlayers; i++) {
			Instances instances = this.instances.get(i);
			ArffSaver saver = this.savers.get(i);
			Instance instance = new DenseInstance(numAttributes);
			instance.setDataset(instances);
			for(String feature: example.getFeatureVector1().keySet()){
				double v1 = example.getFeatureVector1().get(feature);
				double v2 = example.getFeatureVector2().get(feature);
				instance.setValue(firstStateMapping.get(i).get(feature), v1);
				instance.setValue(secondStateMapping.get(i).get(feature), v2);
				instance.setValue(differenceMapping.get(i).get(feature), v1 - v2);
				instance.setValue(divisionMapping.get(i).get(feature), v1 / (v2 + 0.01));
			}
			instance.setValue(decisions.get(i),
					(example.getResults()[i] ? IsFirstStateBetter.Yes.toString()
							: IsFirstStateBetter.No.toString()));
			try {saver.writeIncremental(instance);} catch (IOException e) {}
		}
	}

	/**
	 * Terminates savings & close files
	 * @throws IOException
	 */
	public synchronized void endSave() {
		for(ArffSaver saver: savers)
			try {
				saver.writeIncremental(null);
			} catch (IOException e) {
				e.printStackTrace();
			}
	}

}
