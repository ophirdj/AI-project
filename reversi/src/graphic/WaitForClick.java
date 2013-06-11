package graphic;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WaitForClick implements ActionListener {

	
	private Object lock;

	public WaitForClick(Object lock) {
		this.lock = lock; 
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		synchronized (lock) {
			lock.notify();
		}
	}

}
