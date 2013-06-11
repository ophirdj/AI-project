package graphic;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class BoardListner implements MouseListener{

	GraphicBoard board;
	
	public BoardListner(GraphicBoard board) {
		this.board = board;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		board.addPiece(e.getX(), e.getY());
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		
	}
	
}


