package graphic;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import javax.swing.JButton;
import javax.swing.JFrame;

public class BoardWindow extends JFrame{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4812875566954849142L;

	private GraphicBoard panel= new GraphicBoard();
	private boolean firstShow = true;
	private final int factor = 2;
	private Color[] colors = {Color.WHITE,Color.BLACK,null};
	private String[] names = {"WHITE","BLACK","REMOVE"};
	private ColorButtonPanel buttons= new ColorButtonPanel(colors,names,panel);
	private JButton retExample;
	
	private int MAX_WIDTH;
	private int MAX_HEIGHT;
	private int PRF_WIDTH;
	private int PRF_HEIGHT;
	
	
	public BoardWindow() {
		super();
		calcPrefSize();
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		retExample = new JButton("Save Example");
		retExample.addActionListener(new WaitForClick(this));
		this.add(retExample,BorderLayout.WEST);
		
		this.add(panel,BorderLayout.CENTER);
		this.add(buttons,BorderLayout.SOUTH);
	}
	
	
	private void calcPrefSize() {
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		MAX_WIDTH = gd.getDisplayMode().getWidth();
		MAX_HEIGHT = gd.getDisplayMode().getHeight();
		PRF_WIDTH = MAX_WIDTH/factor;
		PRF_HEIGHT = MAX_HEIGHT/factor;
	}
	
	public Color[][] createExample(Color[][] grid){
		if(this.isVisible() == false){
			showBoard(grid);
		}
		synchronized (this) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return panel.getBoard();
	}


	public void showBoard(Color[][] grid){
		panel.displayBoard(grid);
		if(firstShow){
			this.setVisible(true);
			firstShow = false;
		}
		int height_fix = getHeight() - 2*getContentPane().getHeight();
		int width_fix = getWidth() - 2*getContentPane().getWidth();
		int width = grid[0].length + width_fix;
		int height = grid.length + height_fix;
		setMinimumSize(new Dimension(width,height));
		while(width < PRF_WIDTH){
			width += grid[0].length;
		}
		while(height < PRF_HEIGHT){
			height += grid.length;
		}
		setSize(new Dimension(width,height));
		while(width < PRF_WIDTH){
			width += grid[0].length;
		}
		while(height < PRF_HEIGHT){
			height += grid.length;
		}
		setMaximumSize(new Dimension(width - grid[0].length,height - grid.length));
		setLocationRelativeTo(null);
	}
	
	public void setColor(Color c){
		panel.setColor(c);
	}
	
}