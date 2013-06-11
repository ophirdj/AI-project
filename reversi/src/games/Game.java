package games;

import java.util.List;
import java.util.Map;
import java.util.Random;

import weka.core.Attribute;

/**
 * 
 * @author Ziv Ronen, Ophir De Jager
 *An interface for an object that represent a game in specific state.
 */
public abstract class Game {
	
	/**
	 * @author Ziv Ronen, Ophir De Jager
	 * Thrown when no next state is available.
	 */
	public class NoNextStateException extends Exception{
		private static final long serialVersionUID = 1L;
		}
	
	/**
	 * 
	 * @author Ophir De Jager
	 * Thrown when given game is a terminal state but was expected to be non-terminal
	 */
	public class NotTerminalStateException extends Exception{
		private static final long serialVersionUID = 2L;}

	private List<Game> randomLeft;
	private Random rnd;	
	
	public Game() {
		randomLeft = null;
		rnd = new Random();	
	}
	
	/**
	 * A predicate to determine that the game ended.
	 * @return True if and only if the state is final state.
	 */
	public abstract boolean isTerminalState();
	
	/**
	 * 
	 * @return The index of current player (start with zero).
	 */
	public abstract int getCurrentPlayer();
	
	/**
	 * 
	 * @return the number of players
	 */
	public abstract int getNumPlayers();
	
	/**
	 * Return the value of the game if the game was over for the given player.
	 * @param player The player we want the value for.
	 * @return the value (a number in the range [0,100]) of the game if the game is terminated.
	 * @throws NotTerminalStateException if game did not terminate
	 */
	public abstract int goalValue(int player) throws NotTerminalStateException;
	
	/**
	 * Return a new successor.
	 * @return a successor state that was not returned before.
	 * @throws NoNextStateException If the game ended or all the states was given already.
	 */
	public Game getNextRandomState() throws NoNextStateException{
		if(randomLeft == null) randomLeft = getSuccessors();
		if(randomLeft.isEmpty()) throw new NoNextStateException();
		return randomLeft.remove(rnd.nextInt(randomLeft.size()));
	}
	
	/**
	 * A predicate to determine that next state exists.
	 * @return True if and only if getNextRandomState() will return a value.
	 */
	public boolean hasRandomState(){
		if(randomLeft == null) randomLeft = getSuccessors();
		return !randomLeft.isEmpty();
	}
	
	
	
	
	
	/**
	 * 
	 * @return a mapping from feature name to its game value.
	 */
	public abstract Map<String, Double> getFeaturesValues();
	
	/**
	 * || for tests/experiments only!  should not be used in learning!! ||
	 * Value for heuristic function given by the user.
	 * @return the heuristic value of the state.
	 */
	public abstract double getHeuristic();
	
	/**
	 * 
	 * @return all the successors of the game.
	 */
	public abstract List<Game> getSuccessors();
	
	/**
	 * Display the game
	 */
	public abstract void show();
}
