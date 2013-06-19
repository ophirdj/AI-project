package classifiers;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import weka.classifiers.Classifier;
import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.trees.J48;
import weka.core.Instances;

public class J48Pruned {
	
	private static final float DELTA_CONFIDENCE = 0.05F;
	private static final float MAX_CONFIDENCE = 0.5F;
	private static final int NUM_INTERVALS = Math.round((MAX_CONFIDENCE / DELTA_CONFIDENCE));
	
	private static final Object lock = new Object();

	private static Random rnd = new Random();
	
	private static double bestClassificationRate = Double.NEGATIVE_INFINITY;
	private static J48 bestClassifier = null;

	public static Classifier buildFrom(Instances dataset) {
//		if(dataset == null) return null;
//		J48 j48 = new J48();
//		j48.setUnpruned(false);
//		j48.setMinNumObj(5);
//		j48.setNumFolds(10);
//		j48.setCollapseTree(true);
//		j48.setSubtreeRaising(true);
//		j48.setUseLaplace(true);
//		j48.setUseMDLcorrection(true);
//		try {j48.buildClassifier(dataset);}
//		catch (Exception e) {return null;}
//		return j48;
		if(bestClassifier == null) tune(dataset);
		try {
			return J48.makeCopy(bestClassifier);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void tune(Instances dataset){
		int cores = Runtime.getRuntime().availableProcessors();
		int confidencePartition = NUM_INTERVALS / cores;
		int confidenceMaster = NUM_INTERVALS - confidencePartition * (cores - 1);
		List<Thread> threads = new ArrayList<Thread>(cores-1);
		for(int i = 0; i < cores - 1; i++){
			threads.add(new Thread(new TuneWorker(dataset, confidencePartition, i)));
		}
		for(Thread thread: threads){
			thread.start();
		}
		float confidence = DELTA_CONFIDENCE * (confidenceMaster + 1);
		tunning(dataset, confidence, MAX_CONFIDENCE + DELTA_CONFIDENCE / 2);
		for(Thread thread: threads){
			try {thread.join();} catch (InterruptedException e) {}
		}
		
		System.out.println("end");
		System.out.println("num obj = " + ((100.0*bestClassifier.getMinNumObj()) / dataset.size()) + "%");
		System.out.println("collapse = " + bestClassifier.getCollapseTree());
		System.out.println("subtree = " + bestClassifier.getSubtreeRaising());
		System.out.println("laplace = " + bestClassifier.getUseLaplace());
		System.out.println("MDL = " + bestClassifier.getUseMDLcorrection());
		System.out.println("confidence = " + bestClassifier.getConfidenceFactor());
		System.out.println();
		System.out.println("rate = " + bestClassificationRate);
		System.out.println();
		System.out.println("tree:\n" + bestClassifier);
		
		ClassifierSaver.saveClassifier("./best", bestClassifier);
	}
	
	
	private static class TuneWorker implements Runnable{

		private Instances dataset;
		private float minConfidence;
		private float maxConfidence;

		public TuneWorker(Instances dataset, int confidencePartition, int i) {
			this.dataset = dataset;
			this.minConfidence = confidencePartition * i * DELTA_CONFIDENCE + DELTA_CONFIDENCE;
			this.maxConfidence = confidencePartition * (i + 1) * DELTA_CONFIDENCE + DELTA_CONFIDENCE;
		}

		@Override
		public void run() {
			tunning(dataset, minConfidence, maxConfidence);
		}
		
	}
	
	
	public static void tunning(Instances dataset, float minConfidence, float maxConfidence){
		//boolean unprunned = false;
		int minNumObj = Math.max(2, (int)Math.ceil(dataset.size() / 100.0));
		int maxNumObj = dataset.size()<<1;
		boolean collapseTree = true;
		boolean subtreeRaising = true;
		boolean useLaplace = true;
		boolean useMDLcorrection = false;
		int maxBoolVals = 1<<4;
		for(int numObj = maxNumObj; numObj > minNumObj; numObj /= 1.2){
			for(float confidence = minConfidence; confidence < maxConfidence; confidence += DELTA_CONFIDENCE){
				if(confidence > MAX_CONFIDENCE) confidence = MAX_CONFIDENCE;
//			for(int bool = 0; bool < maxBoolVals; bool++){
//				System.out.println(bool);
//				collapseTree = (bool & 1) == 0;
//				subtreeRaising = (bool & 2) == 0;
//				useLaplace = (bool & 4) == 0;
//				useMDLcorrection = (bool & 8) == 0;
				System.out.println(confidence);
				J48 j48 = new J48();
				j48.setUnpruned(false);
				j48.setNumFolds(10);
				j48.setMinNumObj(numObj);
				j48.setCollapseTree(collapseTree);
				j48.setSubtreeRaising(subtreeRaising);
				j48.setUseLaplace(useLaplace);
				j48.setUseMDLcorrection(useMDLcorrection);
				j48.setConfidenceFactor(confidence);
				try {
					j48.buildClassifier(dataset);
					Evaluation e = new Evaluation(dataset);
					e.crossValidateModel(j48, dataset, 10, rnd);
					synchronized(lock){
						if(e.pctCorrect() > bestClassificationRate){
							bestClassificationRate = e.pctCorrect();
							bestClassifier = j48;
						}
					}
				} catch (Exception e) {
					System.out.println("error");
				}
//			}
			}
		}
	}

}
