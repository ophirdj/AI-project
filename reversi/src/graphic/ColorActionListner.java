package graphic;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ColorActionListner implements ActionListener {

	private Color color;
	private GraphicBoard panel;

	public ColorActionListner(Color color,GraphicBoard panel) {
		this.color = color;
		this.panel = panel;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		panel.setColor(color);
	}

}
