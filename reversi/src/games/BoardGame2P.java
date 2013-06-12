package games;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * class for representing 2 player board game (generic)
 * @author Ophir De Jager
 *
 */
public abstract class BoardGame2P extends Game{
	/**
	 * player names
	 * @author Ophir De Jager
	 *
	 */
	protected enum Players{
		BLACK {
			@Override
			public Players next() {
				return WHITE;
			}

			@Override
			public int index() {
				return 1;
			}
		}
		,
		WHITE {
			@Override
			public Players next() {
				return BLACK;
			}

			@Override
			public int index() {
				return 0;
			}
		}
		,
		EMPTY {
			@Override
			public Players next() {
				return EMPTY;
			}

			@Override
			public int index() {
				return -1;
			}
		}
		;
		/**
		 * 
		 * @return
		 */
		public abstract Players next();
		public abstract int index();
	}
	
	/**
	 * board
	 */
	protected Players[][] board;
	
	/**
	 * board size
	 */
	protected final int boardSize;
	
	public BoardGame2P(Integer size) {
		board = new Players[size][size];
		boardSize = size;
		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize; j++) {
				board[i][j] = Players.EMPTY;
			}
		}
	}
	
	public BoardGame2P(BoardGame2P boardGame) {
		int size = boardGame.boardSize;
		board = new Players[size][size];
		boardSize = size;
		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize; j++) {
				board[i][j] = boardGame.board[i][j];
			}
		}
	}
	
	/**
	 * 
	 * @return list of player names
	 */
	public static List<String> getPlayers(){
		List<String> players = new ArrayList<String>();
		players.add("WHITE");
		players.add("BLACK");
		return players;
	}

	/**
	 * 
	 * @param boardSize - board size
	 * @return the list of features used to value a board
	 */
	public static List<String> getFeatures(Integer boardSize) {
		List<String> features = new ArrayList<String>();
		features.add(new String("num pieces (white)"));
		features.add(new String("num pieces (black)"));
		features.add(new String("num pieces (white - black)"));
		features.add(new String("num pieces (white / black)"));
		for(int i = 0; i < boardSize; i++) features.add(new String("num pieces (white) in row " + (i+1)));
		for(int i = 0; i < boardSize; i++) features.add(new String("num pieces (black) in row " + (i+1)));
		for(int i = 0; i < boardSize; i++) features.add(new String("num pieces (white) in col " + (i+1)));
		for(int i = 0; i < boardSize; i++) features.add(new String("num pieces (black) in col " + (i+1)));
		features.add(new String("num pieces (white) in centre small"));
		features.add(new String("num pieces (black) in centre small"));
		features.add(new String("num pieces (white - black) in centre small"));
		features.add(new String("num pieces (white / black) in centre small"));
		features.add(new String("num pieces (white) in centre large"));
		features.add(new String("num pieces (black) in centre large"));
		features.add(new String("num pieces (white - black) in centre large"));
		features.add(new String("num pieces (white / black) in centre large"));
		features.add(new String("num pieces (white) in corners"));
		features.add(new String("num pieces (black) in corners"));
		features.add(new String("num pieces (white - black) in corners"));
		features.add(new String("num pieces (white / black) in corners"));
		for(int i = 0; i < 2*boardSize - 1; i++) features.add(new String("num pieces (white) in main diagonal " + (i+1)));
		for(int i = 0; i < 2*boardSize - 1; i++) features.add(new String("num pieces (black) in main diagonal " + (i+1)));
		for(int i = 0; i < 2*boardSize - 1; i++) features.add(new String("num pieces (white) in sub diagonal " + (i+1)));
		for(int i = 0; i < 2*boardSize - 1; i++) features.add(new String("num pieces (black) in sub diagonal " + (i+1)));
		features.add(new String("minimal piece distance to end (white)"));
		features.add(new String("minimal piece distance to end (black)"));
		features.add(new String("minimal piece distance to end (white - black)"));
		features.add(new String("minimal piece distance to end (white / black)"));
		features.add(new String("number of legal moves (white)"));
		features.add(new String("number of legal moves (black)"));
		features.add(new String("number of legal moves (white - black)"));
		features.add(new String("number of legal moves (white / black)"));
		features.add(new String("white player turn"));
		features.add(new String("turn number"));
		return features;
	}
	
	@Override
	public Map<String, Double> getFeaturesValues() {
		Map<String, Double> values = new HashMap<String, Double>();
		List<String> features = getFeatures(boardSize);
		
		double numOfWhitePieces = numOfPieces(Players.WHITE);
		double numOfBlackPieces = numOfPieces(Players.BLACK);
		AddFourFeature(values,features,numOfWhitePieces,numOfBlackPieces);
		
		for(int i = 0; i < boardSize; i++) values.put(features.remove(0) , numOfPiecesRow(Players.WHITE,i));
		for(int i = 0; i < boardSize; i++) values.put(features.remove(0) , numOfPiecesRow(Players.BLACK,i));
		for(int j = 0; j < boardSize; j++) values.put(features.remove(0) , numOfPiecesCol(Players.WHITE,j));
		for(int j = 0; j < boardSize; j++) values.put(features.remove(0) , numOfPiecesCol(Players.BLACK,j));
		
		double numOfPiecesWhiteCenterSmall = numOfPiecesCenter(Players.WHITE,2-(boardSize&1));
		double numOfPiecesBlackCenterSmall = numOfPiecesCenter(Players.BLACK,2-(boardSize&1));
		AddFourFeature(values,features,numOfPiecesWhiteCenterSmall,numOfPiecesBlackCenterSmall);
		
		
		double numOfPiecesWhiteCenterLarge = numOfPiecesCenter(Players.WHITE,4-(boardSize&1));
		double numOfPiecesBlackCenterLarge = numOfPiecesCenter(Players.BLACK,4-(boardSize&1));
		AddFourFeature(values,features,numOfPiecesWhiteCenterLarge,numOfPiecesBlackCenterLarge);
		
		double numOfPiecesWhiteCorner = numOfPiecesCorner(Players.WHITE,1);
		double numOfPiecesBlackCorner = numOfPiecesCorner(Players.BLACK,1);
		AddFourFeature(values,features,numOfPiecesWhiteCorner,numOfPiecesBlackCorner);
		
		for(int i = 0; i < 2*boardSize - 1; i++) values.put(features.remove(0) , numOfPiecesMainDiagonal(Players.WHITE,i));
		for(int i = 0; i < 2*boardSize - 1; i++) values.put(features.remove(0) , numOfPiecesMainDiagonal(Players.BLACK,i));
		for(int i = 0; i < 2*boardSize - 1; i++) values.put(features.remove(0) , numOfPiecesSubDiagonal(Players.WHITE,i));
		for(int i = 0; i < 2*boardSize - 1; i++) values.put(features.remove(0) , numOfPiecesSubDiagonal(Players.BLACK,i));
		
		double minimalDistanceWhite = minimalDistanceUp(Players.WHITE);
		double minimalDistanceBlack = minimalDistanceDown(Players.BLACK);
		AddFourFeature(values,features,minimalDistanceWhite,minimalDistanceBlack);
		
		double numOfLegalMovesWhite = numOfLegalMoves(Players.WHITE);
		double numOfLegalMovesBlack = numOfLegalMoves(Players.BLACK);
		AddFourFeature(values,features,numOfLegalMovesWhite,numOfLegalMovesBlack);
		
		values.put(features.remove(0) , isWhitePlayerTurn());
		values.put(features.remove(0) , getTurnNumber());
		assert(features.isEmpty());
		return values;
	}

	/**
	 * 
	 * @return how many turns have passed since the beginning of the current game
	 */
	protected abstract double getTurnNumber();

	/**
	 * 
	 * @return 1 if i'ts the white player's turn and 0 otherwise
	 */
	private double isWhitePlayerTurn() {
		return 1 - getCurrentPlayer();
	}

	/**
	 * helping method for quickly add 4 (related) features
	 * @param values - output parameter values will be added to
	 * @param features - list of attributes
	 * @param paramWhite - white value
	 * @param paramBlack - black value
	 */
	private void AddFourFeature(Map<String, Double> values,
			List<String> features, double paramWhite,
			double paramBlack) {
		values.put(features.remove(0) , paramWhite);
		values.put(features.remove(0) , paramBlack);
		values.put(features.remove(0) , paramWhite - paramBlack);
		values.put(features.remove(0) , paramWhite / (paramBlack+0.1));
	}

	/**
	 * 
	 * @param p - player
	 * @return number of moves player can do
	 */
	protected abstract double numOfLegalMoves(Players p);

	/**
	 * 
	 * @param p - player
	 * @return index of lowest player piece
	 */
	protected double minimalDistanceDown(Players p) {
		for (int i = boardSize - 1; i >= 0; i--) {
			for (int j = 0; j < boardSize; j++) {
				if(p == board[i][j]) return (boardSize-1)-i;
			}
		}
		return boardSize;
	}

	/**
	 * 
	 * @param p - player
	 * @return index of highest player piece
	 */
	protected double minimalDistanceUp(Players p) {
		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize; j++) {
				if(p == board[i][j]) return i;
			}
		}
		return boardSize;
	}

	/**
	 * 
	 * @param p - player
	 * @param diagonal - diagonal number
	 * @return number of player pieces in given diagonal (top left to bottom right)
	 */
	protected double numOfPiecesMainDiagonal(Players p, int diagonal) {
		int count = 0;
		if( diagonal < boardSize){
			for (int j = diagonal; j < boardSize; j++) {
				if(p == board[j-diagonal][j]) count++;
			}
		}
		else{
			int startValue = diagonal - boardSize + 1;
			for (int i = startValue; i < boardSize; i++) {
				if(p == board[i][i-startValue]) count++;
			}
		}
		return count;
	}
	
	/**
	 * 
	 * @param p - player
	 * @param diagonal - diagonal number
	 * @return number of player pieces in given diagonal (top right to bottom left)
	 */
	protected double numOfPiecesSubDiagonal(Players p, int diagonal) {
		int count = 0;
		if( diagonal < boardSize){
			for (int j = 0; j <= diagonal; j++) {
				if(p == board[diagonal-j][j]) count++;
			}
		}
		else{
			int value = diagonal - boardSize + 1;
			for (int i = value; i < boardSize; i++) {
				if(p == board[i][diagonal-i]) count++;
			}
		}
		return count;
	}

	/**
	 * 
	 * @param p - player
	 * @param cornerSize - size of square edge from corner
	 * @return number of player pieces in corners (and adjacent cells)
	 */
	protected double numOfPiecesCorner(Players p, int cornerSize) {
		int count = 0;
		for (int i = 0; i < cornerSize; i++) {
			for (int j = 0; j < cornerSize; j++) {
				if(p == board[i][j]) count++;
			}
		}
		for (int i = 0; i < cornerSize; i++) {
			for (int j = boardSize - cornerSize; j < boardSize; j++) {
				if(p == board[i][j]) count++;
			}
		}
		for (int i = boardSize - cornerSize; i < boardSize; i++) {
			for (int j = 0; j < cornerSize; j++) {
				if(p == board[i][j]) count++;
			}
		}
		for (int i = boardSize - cornerSize; i < boardSize; i++) {
			for (int j = boardSize - cornerSize; j < boardSize; j++) {
				if(p == board[i][j]) count++;
			}
		}
		return count;
	}

	/**
	 * 
	 * @param p - player
	 * @param edgeSize -  size of square edge around center
	 * @return number of player pieces in center
	 */
	protected double numOfPiecesCenter(Players p,int edgeSize) {
		int count = 0;
		int fixDirection = (edgeSize-1)/2;
		int center = boardSize/2;
		int d = (boardSize + 1) & 1;
		int startRow = center-d-fixDirection;
		int startCol = center-d-fixDirection;;
		int endRow = center+fixDirection;
		int endCol = center+fixDirection;
		for (int i = startRow; i <= endRow; i++) {
			for (int j = startCol; j <= endCol; j++) {
				if(p == board[i][j]) count++;
			}
		}
		return count;
	}

	/**
	 * 
	 * @param p - player
	 * @param j - column index
	 * @return number of player pieces in given column
	 */
	protected double numOfPiecesCol(Players p, int j) {
		int count = 0;
		for (int i = 0; i < board.length; i++) {
			if(board[i][j] == p) count++;
		}
		return count;
	}
	
	
	/**
	 * 
	 * @param p - player
	 * @param i - row index
	 * @return number of player pieces in given row
	 */
	protected double numOfPiecesRow(Players p, int i) {
		int count = 0;
		for (int j = 0; j < boardSize; j++) {
			if(board[i][j] == p) count++;
		}
		return count;
	}

	/**
	 * 
	 * @param p - player
	 * @return number of player pieces in the board
	 */
	protected double numOfPieces(Players p) {
		int count = 0;
		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize; j++) {
				if(board[i][j] == p) count++;
			}
		}
		return count;
	}
	
	protected boolean inBoard(int i, int j) {
		if(i < 0 || j < 0) return false;
		return (i < boardSize && j < boardSize);
	}
	
}
