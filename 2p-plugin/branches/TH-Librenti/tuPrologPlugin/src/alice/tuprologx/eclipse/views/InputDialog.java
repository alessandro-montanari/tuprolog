package alice.tuprologx.eclipse.views;

import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.ByteArrayInputStream;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTextArea;

import alice.tuprolog.lib.UserContextInputStream;

public class InputDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private UserContextInputStream stream;
	private JTextArea inputText;
	
	public InputDialog() {
		 setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
         setAlwaysOnTop(true);
         initComponent();
         setVisible(true);
	}
	
	private void initComponent() {
		setTitle("Input Console");
		setSize(new Dimension(200,100));
		setLocation(550,350);
		inputText = new JTextArea();
		add(inputText);
		inputText.addKeyListener(new KeyListener()
		{
			public void keyPressed(KeyEvent arg0) {
				if(arg0.getKeyCode() == KeyEvent.VK_ENTER)
				{ 

					stream.putInput(new ByteArrayInputStream(inputText.getText().toString().getBytes()));
					inputText.setText("");
					setVisible(false);
					dispose();
				}
			}
			@Override
			public void keyReleased(KeyEvent arg0) {}
			@Override
			public void keyTyped(KeyEvent arg0) {}
		});
		inputText.requestFocus();
	}

	public void setUserContextInputStream(UserContextInputStream str) {
		stream = str;
	}
}
