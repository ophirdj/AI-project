package exampleModes;

import games.Game;
import games.Game.NoNextStateException;
import games.Game.NotTerminalStateException;
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
		boolean[] results = new boolean[initialState.getNumPlayers()];
		for(int player = 0; player < initialState.getNumPlayers(); player++){
			try {
				results[player] = game1.goalValue(player) > game2.goalValue(player);
			} catch (NotTerminalStateException e) {}
		}
		return new ExampleResult(game1.getFeaturesValues(), game2.getFeaturesValues(), results);
	}

}
