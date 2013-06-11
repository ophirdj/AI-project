package graphic;

import java.awt.Color;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JPanel;

public class ColorButtonPanel extends JPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8699919263174831498L;

	public ColorButtonPanel(Color[] colors,String[] names ,GraphicBoard panel) {
		setLayout(new FlowLayout());
		for (int i = 0; i < colors.length; i++) {
			JButton b = new JButton(names[i]);
			b.addActionListener(new ColorActionListner(colors[i],panel));
			add(b);
		}
	}
}
