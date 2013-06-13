package exampleModes;

import java.util.ArrayList;

import games.Game;
import games.Game.NoNextStateException;
import games.ZeroSumGame;
import saveWekaFormat.ExampleResult;

public class MinmaxExample implements ExampleMode {
	
	
	private static class LimitedStateQueue {
		
		private final int maxSize;
		private ArrayList<Game> queue;

		public LimitedStateQueue(int maxSize){
			this.maxSize = maxSize;
			this.queue = new ArrayList<Game>(maxSize);
		}
		
		public boolean isEmpty(){
			return queue.isEmpty();
		}
		
		public Game dequeue(){
			return queue.remove(0);
		}
		
		public boolean enqueue(Game state){
			while(queue.size() >= maxSize) dequeue();
			return queue.add(state);
		}
		
		public Game head(){
			return queue.get(0);
		}
		
	}
	
	
	

	
	private int depth;


	public MinmaxExample(int depth){
		assert(depth > 0);
		this.depth = depth;
	}
	
	/**
	 * Check if the game is a 2-player zero-sum game
	 * @param game
	 * @return
	 */
	private static boolean isGoodGame(Game game){
		return (game.getNumPlayers() == 2) && ZeroSumGame.class.isInstance(game);
	}
	
	
	@Override
	public ExampleResult getExample(Game initialState) {
		if(!isGoodGame(initialState)) return null;
		LimitedStateQueue game1 = playGameToEnd(initialState);
		LimitedStateQueue game2 = playGameToEnd(initialState);
		if(game1.isEmpty() || game2.isEmpty()) return null;
		Game state1 = game1.head();
		Game state2 = game2.head();
		boolean[] results = new boolean[2];
		boolean res = isBetter(state1, state2);
		results[0] = res;
		results[1] = !res;
		return new ExampleResult(state1.getFeaturesValues(), state2.getFeaturesValues(), results);
	}

	/**
	 * Run a simulation of the game until it finishes
	 * @param state
	 * @return the last <code>depth<code> moves in the game
	 */
	private LimitedStateQueue playGameToEnd(Game state){
		LimitedStateQueue queue = new LimitedStateQueue(depth);
		while(!state.isTerminalState()){
			try {
				queue.enqueue(state);
				state = state.getNextRandomState();
			} catch (NoNextStateException e) {}
		}
		return queue;
	}
	
	
	/**
	 * Check if state1 is better than state2.
	 * Checking is done by developing both states' minmax
	 * trees up to depth <code>depth<code> and calculating the
	 * minmax value of each state.
	 * @param state1
	 * @param state2
	 * @return true if and only if state1 is better than state2 for player 0 (max player)
	 */
	private boolean isBetter(Game state1, Game state2) {
		int minmaxValue1;
		try {
			minmaxValue1 = minmax(state1, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, 	state1.getCurrentPlayer());
			int minmaxValue2 = minmax(state2, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, 	state2.getCurrentPlayer());
			return minmaxValue1 > minmaxValue2;
		} catch (Exception e) {}
		return false;
	}

	/**
	 * Compute game value (for player 0 - max)
	 * @param state
	 * @param depth
	 * @param alpha
	 * @param beta
	 * @param player
	 * @return
	 * @throws Exception 
	 */
	private int minmax(Game state, int depth, int alpha, int beta, int player) throws Exception {
		if(state.isTerminalState()) return state.goalValue(0);
		if(depth <= 0) return 0; //TODO: what should we do if the game didn't end here?
		if(player == 0){
			for(Game child: state.getSuccessors()){
				alpha = Math.max(alpha, minmax(child, depth-1, alpha, beta, 1^player));
				if(beta <= alpha) break;
			}
			return alpha;
		}
		else{
			for(Game child: state.getSuccessors()){
				beta = Math.min(beta, minmax(child, depth-1, alpha, beta, 1^player));
				if(beta <= alpha) break;
			}
			return beta;
		}
	}

	@Override
	public String name() {
		return "minmax";
	}

}
