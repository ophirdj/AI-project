package checking;

import exampleModes.ExampleMode;
import exampleModes.MinmaxExample;
import games.Game;
import games.Reversi;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import classifiers.J48Pruned;
import classifiers.LoadExamples;

import saveWekaFormat.WekaEncoder;

public class Tst {
	
	public static final int NUM_GAMES = 1000;
	
	
	public static void main(String[] args) throws Exception{
		Object initParams[] = new Object[1];
		initParams[0] = new Integer(8);
		GameIdentifier game = new GameIdentifier(Reversi.class, initParams);
		createExamples(game, new MinmaxExample(6), NUM_GAMES);
		J48Pruned.tune(LoadExamples.loadExamples("./reversi/minmax - white.arff"));
	}
	
	
	/**
	 * Create game examples as specified in the given parameters
	 * @param game game to be played
	 * @param mode type of examples to be created
	 * @param numExamples number of examples to be created
	 * @throws Exception if there was a problem with encoder or game
	 */
	private static void createExamples(GameIdentifier game, ExampleMode mode, int numExamples) throws Exception{
		if(numExamples <= 0) return;
		String directory = "./" + game.getGameName().toLowerCase();
		List<String> players = game.getPlayers();
		List<String> features = game.getFeatures();
		List<String> filenames = new ArrayList<String>(players.size());
		for(String player: players) filenames.add(mode.name() + " - " + player.toLowerCase());
		WekaEncoder encoder = new WekaEncoder(features, filenames, directory);
		int cores = Runtime.getRuntime().availableProcessors();
		int examplesPerThread = numExamples / cores;
		int examplesPrimaryThread = numExamples - examplesPerThread * (cores - 1);
		List<Thread> threads = new ArrayList<Thread>(cores-1);
		for(int i = 0; i < cores - 1; i++){
			threads.add(new Thread(new ExampleWorker(game, mode, examplesPerThread, encoder)));
		}
		for(Thread thread: threads){
			thread.start();
		}
		for(int i = 0; i < examplesPrimaryThread; i++){
			encoder.encode(mode.getExample(game.newGame()));
		}
		for(Thread thread: threads){
			thread.join();
		}
		encoder.endSave();
	}
	
	
	
	
	private static class ExampleWorker implements Runnable{
		
		private GameIdentifier game;
		private ExampleMode mode;
		private int numExamples;
		private WekaEncoder encoder;

		public ExampleWorker(GameIdentifier game, ExampleMode mode, int numExamples, WekaEncoder encoder){
			this.game = game;
			this.mode = mode;
			this.numExamples = numExamples;
			this.encoder = encoder;
		}

		@Override
		public void run() {
			for(int i = 0; i < numExamples; i++){
				try {encoder.encode(mode.getExample(game.newGame()));} catch (Exception e) {break;}
			}
		}
		
	}
	
	
	
	
	/**
	 * Class for generically accessing basic game features such as players, features and initial state
	 * @author Ophir De Jager
	 *
	 */
	private static class GameIdentifier {

		private Class<? extends Game> game;
		private Object[] initParams;
		private Constructor<? extends Game> constructor;
		private Method players;
		private Method features;

		
		public GameIdentifier(Class<? extends Game> game, Object... initParams) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
			this.game = game;
			this.initParams = initParams;
			Class<?>[] types = new Class<?>[initParams.length];
			for(int i = 0; i < types.length; i++)
				types[i] = initParams[i].getClass();
			this.constructor = game.getConstructor(types);
			this.players = game.getMethod("getPlayers");
			this.features = game.getMethod("getFeatures", types);
		}
		
		public Game newGame() throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException{
			return constructor.newInstance(initParams);
		}
		
		/**
		 * Get players names.
		 * The game class must support the method: public static List<String> getPlayers()
		 * @return list of players names
		 * @throws IllegalArgumentException
		 * @throws IllegalAccessException
		 * @throws InvocationTargetException
		 */
		@SuppressWarnings("unchecked")
		public List<String> getPlayers() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException{
			return (List<String>) players.invoke(null);
		}
		
		/**
		 * Get features names for the game.
		 * The game class must support the method: public static List<String> getFeatures(<constructor parameters>)
		 * where <constructor parameters> are the parameter types the game constructor needs
		 * @return list of features names
		 * @throws IllegalArgumentException
		 * @throws IllegalAccessException
		 * @throws InvocationTargetException
		 */
		@SuppressWarnings("unchecked")
		public List<String> getFeatures() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException{
			return (List<String>) features.invoke(null, initParams);
		}

		/**
		 * Get the name of the game
		 * @return game name
		 */
		public String getGameName() {
			return game.getSimpleName();
		}
		
	}
}