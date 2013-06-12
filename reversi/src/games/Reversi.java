package games;

import java.util.ArrayList;
import java.util.List;

public class Reversi extends BoardGame2P {

	private int turn;
	private Players player;
	
	public Reversi(Integer size) {
		super(size);
		int startRow = size/2-1;
		int startCol = size/2-1;
		board[startRow][startCol] = Players.WHITE;
		board[startRow+1][startCol] = Players.BLACK;
		board[startRow][startCol+1] = Players.BLACK;
		board[startRow+1][startCol+1] = Players.WHITE;
		turn = 0;
		player = Players.WHITE;
	}
	
	public Reversi(Reversi reversi){
		super(reversi);
		turn = reversi.turn;
		player = reversi.player;
	}

	@Override
	public boolean isTerminalState() {
		return turn + 4 >= boardSize * boardSize;
	}

	@Override
	public int getCurrentPlayer() {
		return (turn & 1);
	}

	@Override
	public int getNumPlayers() {
		return 2;
	}

	@Override
	public int goalValue(int player) throws NotTerminalStateException{
		if(!isTerminalState()) throw new NotTerminalStateException();
		int whiteScore = 0;
		if(numOfPieces(Players.WHITE) > numOfPieces(Players.BLACK)){
			whiteScore = 100;
		}
		if(numOfPieces(Players.WHITE) == numOfPieces(Players.BLACK)){
			return 50;
		}
		if(player == 0){
			return whiteScore;
		}
		return 100-whiteScore;
	}

	@Override
	public double getHeuristic() {
		return numOfPieces(Players.WHITE)-numOfPieces(Players.BLACK);
	}

	@Override
	public List<Game> getSuccessors() {
		List<Game> successors = new ArrayList<Game>((int)numOfLegalMoves(player));
		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize; j++) {
				if(board[i][j] == Players.EMPTY){
					Reversi game = new Reversi(this);
					game.addPiece(i, j);
					successors.add(game);
				}
			}
		}
		return successors;
	}
	
	private boolean addPiece(int i, int j){
		if(board[i][j] != Players.EMPTY) return false;
		board[i][j] = player;
		for (int deltaI = -1; deltaI <= 1; deltaI++) {
			for (int deltaJ = -1; deltaJ <= 1; deltaJ++) {
				if(deltaI!=deltaJ) eatDirection(i,j,deltaI,deltaJ);
			}
		}
		player = player.next();
		turn++;
		return true;
	}

	private void eatDirection(int row, int col, int deltaI, int deltaJ) {
		int i = row + deltaI;
		int j = col + deltaJ;
		while(inBoard(i,j)){
			if(board[i][j] == Players.EMPTY) return;
			else if(board[i][j] == player.next()){
				i += deltaI;
				j += deltaJ;
			}
			else{
				assert(board[i][j] == player);
				break;
			}
		}
		if(!inBoard(i,j)) return;
		i -= deltaI;
		j -= deltaJ;
		while(board[i][j] != player){
			board[i][j] = player;
		}
	}

	@Override
	public void show() {
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				switch (board[i][j]) {
				case WHITE:
					System.out.print("x ");
					break;
				case BLACK:
					System.out.print("b ");
					break;
				case EMPTY:
					System.out.print("_ ");
					break;
				}
			}
			System.out.println();
		}
	}

	@Override
	protected double getTurnNumber() {
		return turn;
	}

	@Override
	protected double numOfLegalMoves(Players p) {
		if(p.index() != getCurrentPlayer()) return 0;
		return boardSize * boardSize - (turn + 4);
	}

}
