package graphic;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

public class GraphicBoard extends JPanel{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 91223206758222526L;
	private int rows;
	private int cols;
	private int recWidth;
	private int recHeight;
	private int startWidth;
	private int startHeight;
	private Color color = Color.WHITE;
	
	
	BoardListner l= new BoardListner(this);
	private Color[][] board;
	
	public GraphicBoard() {
		setBackground(Color.GREEN);
		addMouseListener(l);
	}
	
	public void displayBoard(Color[][] board){
		this.board = board;
		repaint();
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		rows = board.length;
		cols = board[0].length;
		int size = Math.min(getHeight(), getWidth());
		recWidth = size/(cols+2);
		recHeight = size/(rows+2);
		startWidth = (getWidth() - (recWidth*cols))/2;
		startHeight = (getHeight() - (recHeight*rows))/2;
		
		for(int i = 0; i < rows; i++){
			for(int j = 0; j < cols; j++){
				Color color = board[i][j];
				g.setColor(Color.BLACK);
				g.drawRect(startWidth+recWidth * j, startHeight+recHeight * i, recWidth, recHeight);
				if(color != null){
					g.setColor(color);
					g.fillOval(startWidth+recWidth * j, startHeight+recHeight * i, recWidth, recHeight);
				}
			}
		}
	}

	public void addPiece(int x, int y) {
		if(x < startWidth || y < startHeight ) return;

		int j = (x - startWidth)/recWidth;
		int i = (y - startHeight)/recHeight;
		
		if(i < rows && 0<=i && j < cols && 0<=j){
			board[i][j] = color;
			repaint();
		}
	}
	
	public void setColor(Color c){
		this.color = c;
	}

	public Color[][] getBoard() {
		return this.board;
	}

}