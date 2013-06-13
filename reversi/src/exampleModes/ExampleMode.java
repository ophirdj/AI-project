package exampleModes;

import games.Game;
import saveWekaFormat.ExampleResult;

/**
 * Interface for classes that return examples from games
 * @author Ophir De Jager
 *
 */
public interface ExampleMode {

	/**
	 * Create an example of a pair of game states classified for all players
	 * @param initialState initial game state
	 * @return an example as specified in ExampleResult
	 */
	ExampleResult getExample(Game initialState);
	
	/**
	 * 
	 * @return the mode's name
	 */
	String name();
	
}
