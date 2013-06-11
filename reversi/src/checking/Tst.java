package checking;

import games.Game;
import games.Reversi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import saveWekaFormat.WekaEncoder;

public class Tst {
	
	public static final int numGames = 500;

	/**
	 * @param args
	 * @throws NoNextStateException 
	 */
	public static void main(String[] args) throws Exception {	
		String directory = "./reversi";
		
		List<String> players = new ArrayList<String>();
		players.add("WHITE");
		players.add("BLACK");
		
		int boardSize = 8;
		
		List<String> features = Reversi.getFeatures(boardSize);
		
		WekaEncoder encoder;
		try {
			encoder = new WekaEncoder(features, players, directory);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		for(int i = 0; i < numGames; i++){
		
			Game game1 = new Reversi(boardSize);
			Game game2 = new Reversi(boardSize);
			
			while(!game1.isTerminalState()){
				game1 = game1.getNextRandomState();
			}
			while(!game2.isTerminalState()){
				game2 = game2.getNextRandomState();
			}
			
			boolean result = game1.getHeuristic() > game2.getHeuristic();
			boolean[] results = new boolean[2];
			results[0] = result;
			results[1] = !result;
			encoder.encode(game1.getFeaturesValues(),game2.getFeaturesValues(),results);
			
		}
		
		encoder.endSave();
	}
	
//	public static void main(String[] args) throws Exception{
//		int[] board[] = new int[6][6];
//		Random rnd = new Random();
//		for (int i = 0; i < board.length; i++) {
//			for (int j = 0; j < board[i].length; j++) {
//				board[i][j] = rnd.nextInt(2);
//			}
//		}
//		System.out.println();
//		printArray(board);
////		System.out.println();
////		System.out.println("center 0 small: "+numOfPiecesCenter(board,board.length,0,2-(board.length & 1)));
////		System.out.println("center 1 small: "+numOfPiecesCenter(board,board.length,1,2-(board.length & 1)));
////		System.out.println("center 0 large: "+numOfPiecesCenter(board,board.length,0,4-(board.length & 1)));
////		System.out.println("center 1 large: "+numOfPiecesCenter(board,board.length,1,4-(board.length & 1)));
////		System.out.println();
////		System.out.println("corners 0 small: "+numOfPiecesCorner(board,board.length,0,1));
////		System.out.println("corners 1 small: "+numOfPiecesCorner(board,board.length,1,1));
////		System.out.println("corners 0 large: "+numOfPiecesCorner(board,board.length,0,2));
////		System.out.println("corners 1 large: "+numOfPiecesCorner(board,board.length,1,2));
////		for (int i = 0; i < 2 * board.length - 1; i++) {
////			System.out.println();
////			System.out.println("main 0 diagonal " + (i+1) +": "+numOfPiecesMainDiagonal(board,board.length,0,i));
////			System.out.println("main 1 diagonal " + (i+1) +": "+numOfPiecesMainDiagonal(board,board.length,1,i));
////		}
//		for (int i = 0; i < 2 * board.length - 1; i++) {
//			System.out.println();
//			System.out.println("main 0 diagonal " + (i+1) +": "+numOfPiecesSubDiagonal(board,board.length,0,i));
//			System.out.println("main 1 diagonal " + (i+1) +": "+numOfPiecesSubDiagonal(board,board.length,1,i));
//		}
//	}
//	
//	public static void printArray(int[][] board){
//		for (int i = 0; i < board.length; i++) {
//			for (int j = 0; j < board[i].length; j++) {
//				System.out.print(board[i][j]);
//				System.out.print(" ");
//			}
//			System.out.println();
//		}
//	}
//	
//	private static double numOfPiecesCenter(int[][] board,int boardSize,int p,int edgeSize) {
//		int count = 0;
//		int fixDirection = (edgeSize-1)/2;
//		int center = boardSize/2;
//		int d = (boardSize + 1) & 1;
//		int startRow = center-d-fixDirection;
//		int startCol = center-d-fixDirection;;
//		int endRow = center+fixDirection;
//		int endCol = center+fixDirection;
//		for (int i = startRow; i <= endRow; i++) {
//			for (int j = startCol; j <= endCol; j++) {
//				if(p == board[i][j]) count++;
//			}
//		}
//		return count;
//	}
//	
//	private static double numOfPiecesCorner(int[][] board,int boardSize,int p,int cornerSize) {
//		int count = 0;
//		for (int i = 0; i < cornerSize; i++) {
//			for (int j = 0; j < cornerSize; j++) {
//				if(p == board[i][j]) count++;
//			}
//		}
//		for (int i = 0; i < cornerSize; i++) {
//			for (int j = boardSize - cornerSize; j < boardSize; j++) {
//				if(p == board[i][j]) count++;
//			}
//		}
//		for (int i = boardSize - cornerSize; i < boardSize; i++) {
//			for (int j = 0; j < cornerSize; j++) {
//				if(p == board[i][j]) count++;
//			}
//		}
//		for (int i = boardSize - cornerSize; i < boardSize; i++) {
//			for (int j = boardSize - cornerSize; j < boardSize; j++) {
//				if(p == board[i][j]) count++;
//			}
//		}
//		return count;
//	}
//	
//
//	private static double numOfPiecesMainDiagonal(int[][] board,int boardSize,int p,int diagonal) {
//		int count = 0;
//		if( diagonal < boardSize){
//			for (int j = diagonal; j < boardSize; j++) {
//				if(p == board[j-diagonal][j]) count++;
//			}
//		}
//		else{
//			int startValue = diagonal - boardSize + 1;
//			for (int i = startValue; i < boardSize; i++) {
//				if(p == board[i][i-startValue]) count++;
//			}
//		}
//		return count;
//	}
//	
//	private static double numOfPiecesSubDiagonal(int[][] board,int boardSize,int p,int diagonal) {
//		int count = 0;
//		if( diagonal < boardSize){
//			for (int j = 0; j <= diagonal; j++) {
//				if(p == board[diagonal-j][j]) count++;
//			}
//		}
//		else{
//			int value = diagonal - boardSize + 1;
//			for (int i = value; i < boardSize; i++) {
//				if(p == board[i][diagonal-i]) count++;
//			}
//		}
//		return count;
//	}
}
