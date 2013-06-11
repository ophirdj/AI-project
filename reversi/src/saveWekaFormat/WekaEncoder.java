package saveWekaFormat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
	private ArffSaver[] savers;
	private Instances[] instances;

	/**
	 * Initializes a new WEKA encoder
	 * 
	 * @param features
	 *            list of attributes describing the features in the game
	 * @param directoryPath
	 *            path to directory where files will be created
	 * @param playerNames
	 *            list of player names (like black/white)
	 * @throws IOException
	 *             if save files for data could not open
	 */
	public WekaEncoder(List<Attribute> features, List<String> playerNames,
			String directoryPath) throws IOException {
		this.numPlayers = playerNames.size();
		numFeatures = features.size();
		numAttributes = 2 * numFeatures + 1;
		savers = new ArffSaver[numPlayers];
		instances = new Instances[numPlayers];

		ArrayList<Attribute> attInfo = new ArrayList<Attribute>();
		for (Attribute att : features)
			attInfo.add(renameAttribute(att, att.name() + " (state 1)"));
		for (Attribute att : features)
			attInfo.add(renameAttribute(att, att.name() + " (state 2)"));
		ArrayList<String> vals = new ArrayList<String>();
		vals.add(IsFirstStateBetter.Yes.toString());
		vals.add(IsFirstStateBetter.No.toString());
		Attribute att = new Attribute("is first state better?", vals);
		attInfo.add(att);
		
		assert(attInfo.size() == numAttributes);

		for (int i = 0; i < numPlayers; i++) {
			savers[i] = new ArffSaver();
			savers[i].setDestination(new File(directoryPath + "/"
					+ playerNames.get(i) + ".arff"));
			savers[i].setFile(new File(directoryPath + "/" + playerNames.get(i)
					+ ".arff"));
			ArrayList<Attribute> attributes = new ArrayList<Attribute>(attInfo);
			instances[i] = new Instances("state comparison", attributes, 0);
			instances[i].setClassIndex(attributes.size() - 1);
			savers[i].setStructure(instances[i]);
			savers[i].setCompressOutput(true);
			savers[i].setRetrieval(ArffSaver.INCREMENTAL);
		}
	}

	/**
	 * Copy the attribute but give it a new name
	 * 
	 * @param att
	 * @param name
	 * @return
	 */
	private Attribute renameAttribute(Attribute att, String name) {
		return new Attribute(name, att.getMetadata());
	}

	/**
	 * Encode data to WEKA file format
	 * 
	 * @param features1
	 *            feature vector of state 1
	 * @param features2
	 *            feature vector of state 2
	 * @param results
	 *            for each player specifies if the first state is better
	 */
	public void encode(Double[] features1, Double[] features2, boolean[] results) {
		assert (results.length == numPlayers);
		assert (features1.length == numFeatures);
		assert (features2.length == numFeatures);
		for (int i = 0; i < numPlayers; i++) {
			Instance instance = new DenseInstance(numAttributes);
			instance.setDataset(instances[i]);
			for (int j = 0; j < numFeatures; j++) {
				double d1 = features1[j];
				double d2 = features2[j];
				instance.setValue(j, d1);
				instance.setValue(j + numFeatures, d2);
			}
			instance.setValue(numAttributes - 1,
					(results[i] ? IsFirstStateBetter.Yes.toString()
							: IsFirstStateBetter.No.toString()));
			try {savers[i].writeIncremental(instance);} catch (IOException e) {}
		}
	}

	/**
	 * Terminates savings & close files
	 * @throws IOException
	 */
	public void endSave() {
		for(int i = 0; i < numPlayers; i++)
			try {
				savers[i].writeIncremental(null);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	// sample code
//	public static void main(String[] args) {
//		List<Attribute> features = new ArrayList<Attribute>();
//		features.add(new Attribute("num pieces white"));
//		features.add(new Attribute("num pieces black"));
//		List<String> players = new ArrayList<String>();
//		players.add("WHITE");
//		players.add("BLACK");
//		try {
//			WekaEncoder encoder = new WekaEncoder(features, players,
//					"./reversi");
//			Double[] f1 = new Double[2];
//			Double[] f2 = new Double[2];
//			boolean[] res1 = new boolean[2];
//			boolean[] res2 = new boolean[2];
//			f1[0] = 0.0;
//			f1[1] = 1.0;
//			f2[0] = 1.0;
//			f2[1] = 0.0;
//			res1[0] = false;
//			res1[1] = true;
//			res2[0] = true;
//			res2[1] = false;
//			
//			//save all instances
//			for(int i = 0; i < 50000; i++){
//				encoder.encode(f1, f2, res1);
//				encoder.encode(f2, f1, res2);	
//			}
//			
//			//close files n' stuff...
//			encoder.endSave();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}

}
