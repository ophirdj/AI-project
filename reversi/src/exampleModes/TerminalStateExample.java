package exampleModes;

import games.Game;
import games.Game.NoNextStateException;
import saveWekaFormat.ExampleResult;

public class TerminalStateExample implements ExampleMode {

	@Override
	public ExampleResult getExample(Game initialState) {
		Game game1 = initialState;
		Game game2 = initialState;
		while(!game1.isTerminalState()){
			try {game1 = game1.getNextRandomState();} catch (NoNextStateException e) {}
		}
		while(!game2.isTerminalState()){
			try {game2 = game2.getNextRandomState();} catch (NoNextStateException e) {}
		}
		boolean result = game1.getHeuristic() > game2.getHeuristic();
		boolean[] results = new boolean[2];
		results[0] = result;
		results[1] = !result;
		return new ExampleResult(game1.getFeaturesValues(), game2.getFeaturesValues(), results);
	}

}
