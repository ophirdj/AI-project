package checking;

import exampleModes.ExampleMode;
import exampleModes.TerminalStateExample;
import games.Game;
import games.Reversi;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import saveWekaFormat.WekaEncoder;

public class Tst {
	
	public static final int NUM_GAMES = 100;
	
	
	public static void main(String[] args) throws Exception{
		Object initParams[] = new Object[1];
		initParams[0] = new Integer(8);
		GameIdentifier game = new GameIdentifier(Reversi.class, initParams);
		createExamples(game, new TerminalStateExample(), NUM_GAMES);
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
		WekaEncoder encoder = new WekaEncoder(features, players, directory);
		
		for(int i = 0; i < numExamples; i++){
			encoder.encode(mode.getExample(game.newGame()));
		}
		encoder.endSave();
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